# 軟體設計模式精通之旅

> 「用一趟旅程的時間，成為硬核的 Coding 高手。」
> 透過將「無形變有形」掌握高效率的迭代式設計思路，借鏡 Christopher Alexander 的細膩視角解析設計模式。用數個月的時間，累積相當於七年的進階軟體架構設計能力，最終具備獨立分析、設計與開發實戰框架的實力。

本專案收錄了多個以物件導向設計原則與設計模式為核心的實戰練習，透過由淺入深的題目與嚴謹的架構拆解，持續鍛鍊獨立思考與高品質的程式碼重構能力。

---

## 📁 專案目錄結構

| 子專案資料夾 | 專案名稱與主題 | 核心設計模式 |
| :--- | :--- | :--- |
| [`GYM2-matchmaking-system/`](./GYM2-matchmaking-system/) | **交友配對系統**[cite: 2] | 策略模式 (Strategy Pattern) |
| [`GYM3-card-game-framework/`](./GYM3-card-game-framework/) | **牌類遊戲框架**[cite: 2] | 樣板方法模式 (Template Method Pattern) |

---

## 🚀 子專案導覽

### 1. 策略模式——你的配對系統
* **子專案路徑**：`./GYM2-matchmaking-system/`[cite: 2]
* **主題簡介**：開發一個支援多種配對策略（距離先決、興趣先決）的交友系統，並挑戰動態策略切換與無類別組合爆炸的反向策略 (Reverse Strategy)。
* **詳細說明**：請參考 [GYM2 README](./GYM2-matchmaking-system/README.md)

### 2. 樣板方法——牌類遊戲框架
* **子專案路徑**：`./GYM3-card-game-framework/`[cite: 2]
* **主題簡介**：整合「簡易撲克牌比大小」與「簡易 UNO」兩款遊戲，利用樣板方法將共同流程與玩家/牌堆互動抽象化，將重複程式碼減至最低。
* **詳細說明**：請參考 [GYM3 README](./GYM3-card-game-framework/README.md)

---

## 💡 設計模式學習精神
每個專案皆嚴格遵循道館的黃金思維脈絡：
1. **OOA (物件導向分析)**：捕捉行為變動性與擴充性 Forces，釐清 Problem[cite: 1]。
2. **OOD (物件導向設計)**：透過適當的設計模式解決 Problem，並產出清晰的 UML 類別圖與循序圖[cite: 1]。