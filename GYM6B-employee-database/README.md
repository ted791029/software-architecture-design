# 代理人模式——【延遲載入】員工資料表存取

## 📌 系統簡介
本專案為一個 **員工資料表存取工具 (Employee Database)**。使用者輸入員工 ID，就能查詢該員工的完整資料，以及他的直接下屬。

員工資料表可能有上千萬筆資料，因此不能一次全部載入記憶體；查詢下屬時也要等到真的需要時才載入，存取資料庫前還要先通過密碼檢查。本專案透過 **代理人模式 (Proxy Pattern)**，在**不修改既有 `Database`、`Employee`、`RealDatabase`、`RealEmployee` 設計**的前提下，加入「延遲載入」與「密碼保護」這兩種存取控制。

---

## 🛠️ 核心規則與系統功能

### 1. 員工資料表格式
- 第一列為 Header，共 4 個欄位：`id`、`name`、`age`、`subordinateIds`，欄位之間以空白隔開。
- `subordinateIds` 為直接下屬的 ID，以單一逗號分隔；沒有下屬時該欄位為空。
- 第 `i` 列必為 ID 為 `i - 1` 的員工（例如第 2 列為 ID = 1 的員工）。

```text
id name age subordinateIds
1 waterball 25
2 fixiabis 15 1,3
3 fong 7 1
4 cc 18 1,2,3
```

本專案的範例資料為 [`src/main/resources/database.txt`](./src/main/resources/database.txt)，共 2,000 筆員工資料。

### 2. 查詢功能
- **輸入**：員工 ID。
- **輸出**：該員工的 ID、年齡、姓名，以及直接下屬列表。

### 3. 存取控制需求
| 需求 | 說明 |
| :--- | :--- |
| **防止記憶體耗盡 (Out Of Memory Prevention)** | 不能一次把所有員工資料載入記憶體；要查哪一位員工，就直接跳到該員工所在的那一列讀取。 |
| **延遲載入 (Lazy Initialization)** | 取得員工時先不載入下屬，直到呼叫 `employee.getSubordinates()` 時，才真正從資料庫載入直接下屬。 |
| **密碼保護 (Password Protection)** | 存取 `RealDatabase.getEmployeeById()` 前必須先檢查密碼，密碼必須為 `1qaz2wsx`，否則中斷存取。 |

---

## 🎯 設計需求（非功能性需求）

- **不修改既有設計**：在不修改 `Database`、`Employee` 介面以及 `RealDatabase`、`RealEmployee` 既有程式碼的前提下，擴充設計元素來滿足「延遲載入」與「密碼保護」。
- **透明度**：Client 只依賴 `Database` 與 `Employee`，不需要知道背後是否經過代理。

---

## 🧩 設計架構

### 1. 代理人類別
本專案以兩個代理人分別包裝兩個真實物件 (Real Subject)：

| 代理人 (Proxy) | 代理的介面 (Subject) | 真實物件 (Real Subject) | 代理的種類 | 加入的控制 |
| :--- | :--- | :--- | :--- | :--- |
| `ProtectionPassWordRealDatabaseProxy` | `Database` | `RealDatabase` | **保護代理 (Protection Proxy)** | `getEmployeeById()` 先檢查密碼是否為 `1qaz2wsx`，正確才委派給 `RealDatabase`，否則拋出「密碼錯誤」 |
| `VirtualRealEmployeeProxy` | `Employee` | `RealEmployee` | **虛擬代理 (Virtual Proxy)** | 只先保存 `subordinateIds` 字串；第一次呼叫 `getSubordinates()` 時，才依序呼叫 `Database.getEmployeeById()` 載入下屬並快取到 `RealEmployee`，其餘 getter / setter 直接委派 |

### 2. 防止記憶體耗盡：位移索引檔
`RealDatabase` 不會把員工資料讀進記憶體，而是透過索引檔直接跳到指定的那一列：
1. **建立索引**：第一次啟動時，若索引檔 [`database_idx.bin`](./src/main/resources/database_idx.bin) 不存在，就掃描一次 `database.txt`，把每一列開頭的檔案位移 (byte offset) 以 `long` 寫進索引檔（每 1,000 列寫出一批）。
2. **載入索引**：啟動時只把索引檔讀進記憶體，每列只佔 8 bytes，遠小於完整的員工資料。
3. **查詢**：以 `RandomAccessFile.seek()` 跳到 `id + 1` 列的位移，只讀取那一行並轉成員工物件。

查詢回傳的員工物件就是 `VirtualRealEmployeeProxy`，因此下屬也會被延遲載入。

---

## 📊 OOA / OOD 設計與 UML 架構

### 1. OOA 分析階段圖
* **檔案連結**：[OOA UML 圖檔](./docs/ooa_uml.png)
* **說明**：
  * **Force-存取控制（Database）**：Client 存取 `RealDatabase.getEmployeeById()` 時，希望增加密碼保護。
  * **Force-存取控制（Employee）**：Client 存取 `RealEmployee` 的下屬時，希望延遲載入。
  * **Force-透明度**：不必修改 `Database` 與 `Employee` 介面的定義，也能輕鬆加入控制。
  * **Force-不修改既有程式碼**：希望不修改 `RealDatabase` 的既有程式碼。
  * **Problem**：若直接在 `RealDatabase`、`RealEmployee` 中加入密碼檢查與延遲載入邏輯，會違反「不修改既有設計」的要求，也讓真實物件混入與核心職責無關的控制邏輯。

### 2. OOD 設計階段圖
* **檔案連結**：[OOD UML 圖檔](./docs/ood_uml.png)
* **說明**：
  * 導入 **Proxy Pattern**：新增 `ProtectionPassWordRealDatabaseProxy` 與 `VirtualRealEmployeeProxy`，分別實作 `Database` 與 `Employee`，並以 `real` 關聯持有對應的真實物件。
  * 代理人與真實物件實作相同的介面，因此 Client 拿到的不論是代理人還是真實物件，使用方式都一樣。
  * `VirtualRealEmployeeProxy` 另外持有 `Database`，在 `initSubordinates()` 中依 `subordinateIds` 逐一查詢下屬。

### 3. 系統循序圖 (Sequence Diagram)
* **檔案連結**：[循序圖圖檔](./docs/ood_sequence_diagram.png)
* **說明**：
  * 描繪 Client 呼叫 `Database.getEmployeeById(id)` 後，由 `Database` 建立並回傳 `Employee` 的時序。
  * 整張圖只出現 `Database` 與 `Employee` 兩個介面，說明 Client 完全不知道背後有代理人，符合「透明度」的要求。

---

## ▶️ 執行方式

`Main` 會以「保護代理 → `RealDatabase`」的組合建立 `Client`：
```java
Client client = new Client(new ProtectionPassWordRealDatabaseProxy(new RealDatabase(), password));
client.start();
```

1. 輸入員工 ID。
2. 畫面會印出該員工的 ID、年齡、姓名，以及直接下屬列表（此時才觸發下屬的延遲載入）。
3. 把 `Main` 中的 `password` 改成其他值（例如 `"test"`），即可看到密碼保護中斷存取。
