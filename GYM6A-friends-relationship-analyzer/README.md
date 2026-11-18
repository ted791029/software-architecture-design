# 轉接器模式——好友關係分析器

## 📌 系統簡介
本專案為一個簡易的 **好友關係分析器 (Friends Relationship Analyzer)**。使用者先用特定格式撰寫「好友關係腳本 (Script)」，描述每個人各有哪些好友，再把腳本餵進分析器，就能查詢：
1. **共同好友 (Mutual Friends)**：兩人之間有哪些共同好友。
2. **連結 (Connection)**：兩人之間是否能透過一連串的好友關係連在一起。

本題的重點不在演算法，而在**整合**：Client 端已經依賴自己設計的介面開發完成，但實際的分析能力來自同事小華的 `SuperRelationshipAnalyzer` 與第三方 Graph 套件 **JGraphT**。三者的介面與腳本格式都不相容，而且都**不允許修改原始碼**。本專案透過 **轉接器模式 (Adapter Pattern)** 銜接這些不相容的介面。

---

## 🛠️ 核心規則與系統功能

### 1. 功能需求
- **共同好友**：C 為 A 和 B 的共同好友，若且唯若 A 和 C 是好友，且 C 和 B 也是好友。
- **連結（進階需求）**：A 和 B 之間存在連結，若且唯若存在一串 `A, x1, x2, …, xn, B`，其中每相鄰兩人都是好友。好友關係是雙向的，因此連結也是雙向的。

### 2. 兩種腳本格式
Client 端（`RelationshipAnalyzer`）使用的格式為「某人: 好友清單」：
```text
A: B C D
B: A D E
C: A E G K M
D: A B K P
E: B C J K L
F: Z
```

小華的 `SuperRelationshipAnalyzer` 使用的格式則為「一行一組好友關係」：
```text
A -- B
A -- C
A -- D
B -- D
B -- E
...
```

### 3. 各方提供的介面
| 角色 | 類別 | 提供的操作 | 可否修改 |
| :--- | :--- | :--- | :---: |
| Client 的意圖 | `RelationshipAnalyzer` | `parse(script) : RelationshipGraph`、`getMutualFriends(name1, name2) : List<String>` | ❌ |
| Client 的意圖 | `RelationshipGraph` | `hasConnection(name1, name2) : boolean` | ❌ |
| 小華的實作 | `SuperRelationshipAnalyzer` | `init(script)`、`isMutualFriend(targetName, name1, name2) : boolean` | ❌ |
| 第三方套件 | JGraphT（`SimpleGraph`、`ConnectivityInspector`） | `addVertex()`、`addEdge()`、`pathExists(v1, v2)` | ❌ |

---

## 🎯 設計需求（非功能性需求）

- **不修改既有程式碼**：在完全不修改 Client、`RelationshipAnalyzer`、`RelationshipGraph` 與 `SuperRelationshipAnalyzer` 任何一行程式碼的前提下，完成整合。
- **整合第三方套件**：連結分析屬於圖論的 Connectivity Analysis，需找出並整合現成的 Graph 套件（本專案選用 [JGraphT](https://jgrapht.org/) `jgrapht-core 1.4.0`），而不是自行實作圖論演算法。
- **重複利用**：盡可能重複利用小華與第三方套件既有的能力，來實現 Client 的意圖。

---

## 🧩 設計架構

本專案以兩個 **物件轉接器 (Object Adapter)** 分別包裝兩個不相容的被轉接者 (Adaptee)：

| 轉接器 (Adapter) | 實作的目標介面 (Target) | 被轉接者 (Adaptee) | 轉接方式 |
| :--- | :--- | :--- | :--- |
| `SuperRelationshipAnalyzerAdapter` | `RelationshipAnalyzer` | `SuperRelationshipAnalyzer` | `parse()` 先把「`A: B C D`」格式轉成「`A -- B`」格式再呼叫 `init()`；`getMutualFriends()` 則逐一檢查 `name1` 的每位好友，呼叫 `isMutualFriend()` 篩出共同好友 |
| `JGraphTRelationshipGraphAnalyzerAdapter` | `RelationshipGraphAnalyzer` | JGraphT `SimpleGraph` | 解析腳本，以 `addVertex()` / `addEdge()` 建立無向圖，再包成 `ConnectivityInspector` |
| `JGraphTRelationshipGraph` | `RelationshipGraph` | JGraphT `ConnectivityInspector` | `hasConnection()` 直接委派給 `pathExists()` |

- **`RelationshipGraphAnalyzer`（新增的中介介面）**：`SuperRelationshipAnalyzerAdapter` 不直接依賴 JGraphT，而是依賴這個介面來取得 `RelationshipGraph`。之後若要改用其他 Graph 套件，只要再寫一個實作即可，`SuperRelationshipAnalyzerAdapter` 不用修改。
- **避免重複的邊**：腳本中的好友關係是雙向列出的（`A: B` 與 `B: A`），因此兩個轉接器在展開腳本時，只保留「好友名稱大於自己」的那一組，以免同一段關係被加入兩次。

---

## 📊 OOA / OOD 設計與 UML 架構

### 1. OOA 分析階段圖
* **檔案連結**：[OOA UML 圖檔](./docs/ooa_uml.png)
* **說明**：
  * **Force-透明度**：不能修改 `RelationshipAnalyzer` 與 `RelationshipGraph` 介面，Client 不應察覺背後由誰實作。
  * **Force-相容性**：`SuperRelationshipAnalyzer` 與 JGraphT 都是第三方的依賴，不允許修改原始碼，導致它們無法直接實作 `RelationshipAnalyzer` / `RelationshipGraph`。
  * **Force-重複利用性**：希望能重複利用 `SuperRelationshipAnalyzer` 的 `init()` 與 `isMutualFriend()`，以及 JGraphT 的 `pathExists()`，來實現 Client 的意圖。
  * **Problem**：Client 的意圖與既有實作之間介面不相容、腳本格式也不同，而且雙方都不能修改。

### 2. OOD 設計階段圖
* **檔案連結**：[OOD UML 圖檔](./docs/ood_uml.png)
* **說明**：
  * 導入 **Adapter Pattern**：新增 `SuperRelationshipAnalyzerAdapter`、`JGraphTRelationshipGraphAnalyzerAdapter` 與 `JGraphTRelationshipGraph` 三個轉接類別（圖中綠色），分別實作 Client 端的介面，並在內部委派給被轉接者。
  * 類別依職責分為三個階層：階層 1 為 Client 與其意圖介面，階層 2 為小華的類別與其轉接器，階層 3 為 JGraphT 與其轉接器。上層只依賴介面，不依賴下層的具體實作。

### 3. 系統循序圖 (Sequence Diagram)
* **檔案連結**：[循序圖圖檔](./docs/ood_sequence_diagram.png)
* **說明**：
  * 從 Client 的角度描繪呼叫順序：先以 `parse(script)` 取得 `RelationshipGraph`，再呼叫 `hasConnection()` 判斷兩人是否有連結，最後呼叫 `getMutualFriends()` 取得共同好友。
  * 整張圖只出現 `RelationshipAnalyzer` 與 `RelationshipGraph` 兩個介面，說明 Client 完全不知道背後是由轉接器、小華的類別或 JGraphT 完成工作，達成「透明度」的要求。

---

## ▶️ 執行方式

`Main` 會以內建腳本建立分析器並啟動 `CLI`：
```text
A: B C D
B: A C D E
```

1. 輸入兩個人的名字，以逗號分隔（例如 `A,B`），名字限定為 `A-Z`。
2. 選擇功能：
   - `1`：印出兩人的共同好友。
   - `2`：印出兩人之間是否有連結。
