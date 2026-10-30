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
| [`GYM5-big2-game/`](./GYM5-big2-game/) | **大老二撲克牌遊戲 (魔王題)** | 策略模式 / 複合設計模式 (OCP) |

---

## 🚀 子專案導覽

### 1. 策略模式——你的配對系統
* **子專案路徑**：`./GYM2-matchmaking-system/`
* **主題簡介**：開發一個支援多種配對策略（距離先決、興趣先決）的交友系統，並挑戰動態策略切換與無類別組合爆炸的反向策略 (Reverse Strategy)。
* **詳細說明**：請參考 [GYM2 README](./GYM2-matchmaking-system/README.md)

### 2. 樣板方法——牌類遊戲框架
* **子專案路徑**：`./GYM3-card-game-framework/`
* **主題簡介**：整合「簡易撲克牌比大小」與「簡易 UNO」兩款遊戲，利用樣板方法將共同流程與玩家/牌堆互動抽象化，將重複程式碼減至最低。
* **詳細說明**：請參考 [GYM3 README](./GYM3-card-game-framework/README.md)

### 3. 責任鏈模式——碰撞偵測＆處理
* **子專案路徑**：`./GYM4A-collision-detection/`
* **主題簡介**：開發一維世界的生命碰撞偵測與處理 DEMO 程式，支援 Hero、Water、Fire 多種生物互動與狀態變化，並嚴格遵循開閉原則（OCP），讓未來擴充新生命或碰撞效果時不需修改既有領域模型。
* **詳細說明**：請參考 [GYM4 README](./GYM4A-collision-detection/README.md)

### 4. 魔王題——大老二撲克牌遊戲 (Big-2)
* **子專案路徑**：`./GYM4-Big2/`
* **主題簡介**：高難度綜合實戰演練。完整實作大老二撲克牌遊戲的複雜規則、輸入輸出測資驗證與回合流程控制，並嚴格要求在「不同牌型的支援」上遵循開閉原則 (OCP)。
* **詳細說明**：請參考 [GYM5 README](./GYM4-Big2/README.md)

---

## 💡 設計模式學習精神
每個專案皆嚴格遵循道館的黃金思維脈絡：
1. **OOA (物件導向分析)**：捕捉行為變動性與擴充性 Forces，釐清 Problem。
2. **OOD (物件導向設計)**：透過適當的設計模式解決 Problem，並產出清晰的 UML 類別圖與循序圖。