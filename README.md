# 軟體設計模式精通之旅

> 「用一趟旅程的時間，成為硬核的 Coding 高手。」
> 透過將「無形變有形」掌握高效率的迭代式設計思路，借鏡 Christopher Alexander 的細膩視角解析設計模式。用數個月的時間，累積相當於七年的進階軟體架構設計能力，最終具備獨立分析、設計與開發實戰框架的實力。

本專案收錄了多個以物件導向設計原則與設計模式為核心的實戰練習，透過由淺入深的題目與嚴謹的架構拆解，持續鍛鍊獨立思考與高品質的程式碼重構能力。

---

## 📁 專案目錄結構

| 子專案資料夾 | 專案名稱與主題 | 核心設計模式 |
| :--- | :--- | :--- |
| [`GYM2-matchmaking-system/`](./GYM2-matchmaking-system/) | **交友配對系統** | 策略模式 (Strategy Pattern) |
| [`GYM3-card-game-framework/`](./GYM3-card-game-framework/) | **牌類遊戲框架** | 樣板方法模式 (Template Method Pattern) |
| [`GYM4A-collision-detection/`](./GYM4A-collision-detection/) | **碰撞偵測＆處理** | 責任鏈模式 (Chain of Responsibility Pattern) |
| [`GYM4-Big2/`](./GYM4-Big2/) | **大老二撲克牌遊戲 (魔王題)** | 策略模式 / 複合設計模式 (OCP) |
| [`GYM5A-youtube/`](./GYM5A-youtube/) | **YouTube 訂閱機制** | 觀察者模式 (Observer Pattern) |
| [`GYM5B-shortcut-key/`](./GYM5B-shortcut-key/) | **快捷鍵設置機制** | 指令模式 (Command Pattern) |
| [`GYM5C-map-game/`](./GYM5C-map-game/) | **二維地圖冒險遊戲** | 狀態模式 (State Pattern) |
| [`GYM5-rpg/`](./GYM5-rpg/) | **真・RPG 之對戰遊戲 (魔王題)** | 策略 / 狀態 / 觀察者 / 責任鏈 (複合設計模式) |
| [`GYM6A-friends-relationship-analyzer/`](./GYM6A-friends-relationship-analyzer/) | **好友關係分析器** | 轉接器模式 (Adapter Pattern) |
| [`GYM6B-employee-database/`](./GYM6B-employee-database/) | **【延遲載入】員工資料表存取** | 代理人模式 (Proxy Pattern) |

---

## 🚀 子專案導覽

### 1. 策略模式——你的配對系統
- **子專案路徑**：`./GYM2-matchmaking-system/`
- **主題簡介**：開發一個支援多種配對策略（距離先決、興趣先決）的交友系統，並挑戰動態策略切換與無類別組合爆炸的反向策略 (Reverse Strategy)。
- **詳細說明**：請參考 [GYM2 README](./GYM2-matchmaking-system/README.md)

### 2. 樣板方法——牌類遊戲框架
- **子專案路徑**：`./GYM3-card-game-framework/`
- **主題簡介**：整合「簡易撲克牌比大小」與「簡易 UNO」兩款遊戲，利用樣板方法將共同流程與玩家/牌堆互動抽象化，將重複程式碼減至最低。
- **詳細說明**：請參考 [GYM3 README](./GYM3-card-game-framework/README.md)

### 3. 責任鏈模式——碰撞偵測＆處理
- **子專案路徑**：`./GYM4A-collision-detection/`
- **主題簡介**：開發一維世界的生命碰撞偵測與處理 DEMO 程式，支援 Hero、Water、Fire 多種生物互動與狀態變化，並嚴格遵循開閉原則（OCP），讓未來擴充新生命或碰撞效果時不需修改既有領域模型。
- **詳細說明**：請參考 [GYM4A README](./GYM4A-collision-detection/README.md)

### 4. 魔王題——大老二撲克牌遊戲 (Big-2)
- **子專案路徑**：`./GYM4-Big2/`
- **主題簡介**：高難度綜合實戰演練。完整實作大老二撲克牌遊戲的複雜規則、輸入輸出測資驗證與回合流程控制，並嚴格要求在「不同牌型的支援」上遵循開閉原則 (OCP)。
- **詳細說明**：請參考 [GYM4 README](./GYM4-Big2/README.md)

### 5. 觀察者模式——YouTube 訂閱機制
- **子專案路徑**：`./GYM5A-youtube/`
- **主題簡介**：模擬 YouTube 頻道與訂閱者的互動機制。當頻道上傳新影片時自動發送通知，並讓不同訂閱者（如水球、火球）根據影片長度條件執行按讚或解除訂閱，同時遵循開閉原則 (OCP)。
- **詳細說明**：請參考 [GYM5A README](./GYM5A-youtube/README.md)

### 6. 指令模式——快捷鍵設置機制
- **子專案路徑**：`./GYM5B-shortcut-key/`
- **主題簡介**：模擬軍用主控室的主控制器，允許將鍵盤按鍵 (`a-z`) 靈活綁定至坦克與通訊設備等裝置操作或重置功能，並透過指令模式完美支援 Undo (還原)、Redo (重做) 以及多步驟指令巨集 (Macro)。
- **詳細說明**：請參考 [GYM5B README](./GYM5B-shortcut-key/README.md)

### 7. 狀態模式——二維地圖冒險遊戲
- **子專案路徑**：`./GYM5C-map-game/`
- **主題簡介**：在隨機生成的二維地圖上，主角與怪物輪流移動、攻擊並拾取寶物（無敵星星、毒藥、加速藥水、補血罐、任意門、惡魔果實、王者之印）。角色會因寶物或受傷進入無敵、中毒、加速、恢復、混亂、蓄力、爆發、瞬身等狀態，並透過狀態模式將各狀態的行為與持續回合、狀態轉換封裝於獨立的 State 類別中，讓新增狀態時不需修改角色本身。
- **詳細說明**：請參考 [GYM5C README](./GYM5C-map-game/README.md)

### 8. 魔王題——真・RPG 之對戰遊戲
- **子專案路徑**：`./GYM5-rpg/`
- **主題簡介**：「進階行為型模式」的最終魔王題。實作兩軍輪流行動的回合制戰鬥系統，包含 10 種技能（水球、火球、自我治療、石化、下毒、召喚、自爆、鼓舞、詛咒、一拳攻擊）與多種異常狀態。綜合運用策略、樣板方法、狀態、觀察者與責任鏈模式，讓新增技能或狀態時不需修改核心戰鬥流程，並以輸入輸出測資驗證結果。
- **詳細說明**：請參考 [GYM5 README](./GYM5-rpg/README.md)

### 9. 轉接器模式——好友關係分析器
- **子專案路徑**：`./GYM6A-friends-relationship-analyzer/`
- **主題簡介**：開發一個能分析共同好友與兩人之間連結 (Connectivity) 的好友關係分析器。在完全不修改 Client 端介面、同事小華的 `SuperRelationshipAnalyzer` 與第三方套件 JGraphT 的前提下，透過轉接器模式銜接彼此不相容的介面與腳本格式，並重複利用既有能力實現 Client 的意圖。
- **詳細說明**：請參考 [GYM6A README](./GYM6A-friends-relationship-analyzer/README.md)

### 10. 代理人模式——【延遲載入】員工資料表存取
- **子專案路徑**：`./GYM6B-employee-database/`
- **主題簡介**：為可能多達上千萬筆的員工資料表設計存取工具。透過位移索引檔直接跳到指定列讀取，避免記憶體耗盡；並在不修改既有 `Database`、`Employee` 設計的前提下，以虛擬代理實現下屬的延遲載入 (Lazy Initialization)，以保護代理實現密碼保護。
- **詳細說明**：請參考 [GYM6B README](./GYM6B-employee-database/README.md)

---

## 💡 設計模式學習精神
每個專案皆嚴格遵循道館的黃金思維脈絡：
1. **OOA (物件導向分析)**：捕捉行為變動性與擴充性 Forces，釐清 Problem。
2. **OOD (物件導向設計)**：透過適當的設計模式解決 Problem，並產出清晰的 UML 類別圖與循序圖。