# 策略模式——你的配對策略是什麼呢？

## 📌 系統簡介
本專案為一個**交友配對系統 (Matchmaking System)**，系統會根據設定的策略幫用戶 (Individual) 尋找最適合的配對對象。

---

## 🛠️ 資料結構與需求說明

### 1. 用戶資料欄位 (Individual)
| 欄位 | 型態 | 說明 / 格式 |
| :--- | :--- | :--- |
| **編號 (ID)** | `int` | 正整數 (>0)，每位對象編號唯一。 |
| **性別 (Gender)** | `Enum` | `MALE` 表示男生、`FEMALE` 表示女生。 |
| **年紀 (Age)** | `int` | 正整數，至少 18 歲。 |
| **自我介紹 (Intro)** | `String` | 長度約 0~200 字。 |
| **興趣 (Habits)** | `List<String>` | 逗號隔開的多個字串（每個興趣 1~10 字），如：`打籃球, 煮菜, 玩遊戲`。 |
| **座標 (Coord)** | `(int, int)` | 表示用戶所在的 $(x, y)$ 位置。 |

---

### 2. 核心配對策略

1. **距離先決 (Distance-Based)**
   - **規則**：配對距離最近者；若距離相同，選擇**編號較小**者。
   - **距離公式**：$\text{Distance} = (y - y')^2 + (x - x')^2$

2. **興趣先決 (Habit-Based)**
   - **規則**：配對與自己興趣交集量最大者；若交集量相同，選擇**編號較小**者。

---

## 🎯 設計需求（非功能性需求）

- **動態策略切換**：系統必須允許 Client (如 `Main` 方法) 能在外部靈活抽換配對策略，以應對未來持續新增的新策略。
- **設計模式思考流程 (Forces → Problem → Pattern)**：
  1. **OOA (需求分析)**：捕捉「行為變動性」與「擴充性」Forces 之間的衝突點並總結為 Problem。
  2. **OOD (設計與實作)**：套用 **Strategy Pattern (策略模式)** 解決問題。

---

## 🔥 進階挑戰題：反向策略 (Reverse Strategy)

系統需要支援將策略反向（例如：距離最遠、興趣交集最少），但必須滿足以下限制：

1. **禁止直接耦合反向概念與布林開關**：
   - 配對系統、距離先決、興趣先決等類別中**不可**出現 `reverse: boolean` 欄位或參數。
   - 禁止範例：`new MatchmakingSystem(strategy, reverse=true)`、`strategy.reverse()`。
2. **避免類別組合爆炸 (Combinatorial Explosion)**：
   - **禁止**建立 4 個個別策略類別（如 `DistanceBased`、`ReverseDistanceBased`、`HabitBased`、`ReverseHabitBased`）。
   - *提示：思考如何透過適當的設計模式或裝飾/反轉邏輯（例如 Decorator Pattern 或自訂 Comparator）來達到反向效果。*

---

## 📊 OOA / OOD 設計與 UML 架構

本專案遵循道館要求的 **Forces → Problem → Pattern** 思路進行塑模，相關 UML 圖檔與設計說明如下：

### 1. OOA 分析階段圖
* **檔案連結**：[OOA UML 圖檔](./docs/ooa_uml.png)[cite: 1]
* **說明**：
  * **行為變動性 Force**（以紅色標註）：標記配對演算法隨業務需求變動的熱點（未來可能擴充更多新奇的配對規則）。
  * **擴充性 Force**：希望在擴充新配對策略時，能做到**完全不必修改**系統核心類別 (`MatchmakingSystem`)。
  * **Problem**：當系統需要支援多種動態演算法且頻繁擴充時，若直接使用 `switch-case` 會違反開放封閉原則 (OCP)。

### 2. OOD 設計階段圖
* **檔案連結**：[OOD UML 圖檔](./docs/ood_uml.png)[cite: 1]
* **說明**：
  * 導入 **Strategy Pattern (策略模式)** 後的類別圖（已將 Forces 轉為綠色）。
  * 定義抽象策略介面，並由 `DistanceBasedStrategy` 與 `HabitBasedStrategy` 實作，讓 Client 與主系統能夠透過多型動態抽換策略。