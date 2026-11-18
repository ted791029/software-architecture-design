# 魔王題——真・RPG 之對戰遊戲

## 📌 系統簡介
本專案為軟體設計模式精通之旅「進階行為型模式」的**最終魔王題：真・RPG 之對戰遊戲**。

這是一款回合制戰鬥系統：兩支軍隊（友方與敵方）輪流行動，每位角色在自己的回合中可以選擇「普通攻擊」敵方一位角色，或消耗自身魔力 (MP) 施放特殊技能，直到其中一方被全數殲滅。

本題共有 **10 個技能**，等同 10 道行為變種：治癒技、高傷害單體攻擊、低傷害群體攻擊、異常狀態 (DEBUFF)、召喚、詛咒、犧牲型的自爆⋯⋯每個技能的目標選擇、效果與觸發時機都不相同。本專案的重點在於同時運用 **Bottom-Up（依賴反轉重構三步驟）** 與 **Top-Down（察覺 Forces、套用設計模式）** 兩種思維，讓每一項設計決策都有根據。

---

## ⚔️ 核心領域詞彙與規則

### 1. 基礎詞彙 (Glossary)
- **軍隊 (Troop)**：共兩支軍隊，`軍隊-1` 為玩家方，`軍隊-2` 為敵方。
- **角色 (Role)**：擁有名稱、生命值 (HP)、魔力 (MP)、力量 (STR)、技能清單與目前狀態。
  - **英雄 (Hero)**：`軍隊-1` 中名為「英雄」的角色，由玩家操控。
  - **AI**：其他所有角色皆由電腦操控。
- **行動 (Action)**：普通攻擊或技能，每種行動都有 MP 消耗與目標設定。

### 2. 戰鬥流程 (Battle Flow)
1. 兩支軍隊依序進行回合，軍隊內的角色依照順序輪流行動。
2. 每位角色的回合依序為：
   1. 倒數目前狀態的剩餘回合，歸零時回到正常狀態。
   2. 印出角色的 HP、MP、STR 與狀態。
   3. 觸發狀態效果（例如中毒扣血），若角色因此死亡則跳過行動。
   4. 選擇行動：MP 不足時會提示並要求重新選擇。
   5. 選擇目標：若候選目標多於技能需要的數量，就由角色自行挑選；否則自動選擇全部候選目標。
   6. 扣除 MP 並執行行動。
3. **遊戲結束**：英雄死亡時印出「你失敗了！」；敵方軍隊全滅時印出「你獲勝了！」。

### 3. 行動與技能一覽
| 行動 | MP | 目標 | 效果 |
| :--- | :---: | :--- | :--- |
| 普通攻擊 | 0 | 敵方 1 位 | 造成等同自身 STR 的傷害 |
| 水球 | 50 | 敵方 1 位 | 造成 120 點傷害 |
| 火球 | 50 | 敵方全體 | 對每位敵人造成 50 點傷害 |
| 自我治療 | 50 | 自己 | 回復 150 點 HP |
| 石化 | 100 | 敵方 1 位 | 使目標進入「石化」狀態 |
| 下毒 | 80 | 敵方 1 位 | 使目標進入「中毒」狀態 |
| 召喚 | 150 | 無 | 召喚一隻 Slime（HP 100、MP 0、STR 50，只會普通攻擊）加入己方軍隊；Slime 死亡時，若施放者仍存活則回復 30 點 HP |
| 自爆 | 200 | 所有其他角色 | 施放者立即死亡，並對場上所有其他角色（含友軍）各造成 150 點傷害 |
| 鼓舞 | 100 | 友方 3 位 | 使目標進入「受到鼓舞」狀態 |
| 詛咒 | 100 | 敵方 1 位 | 被詛咒者死亡時，若施放者仍存活，施放者回復等同被詛咒者當下 MP 的 HP |
| 一拳攻擊 | 180 | 敵方 1 位 | 依目標狀況決定傷害（見下表） |

**一拳攻擊的傷害判定**（由上而下，符合第一個條件即套用）：
| 條件 | 效果 |
| :--- | :--- |
| 目標 HP ≥ 500 | 造成 300 點傷害 |
| 目標處於中毒狀態 | 連續造成 3 次 80 點傷害 |
| 目標處於石化狀態 | 連續造成 3 次 80 點傷害 |
| 目標處於受到鼓舞狀態 | 造成 100 點傷害，並使目標回到正常狀態 |
| 目標處於正常狀態 | 造成 100 點傷害 |

### 4. 狀態效果表
| 狀態 | 持續回合 | 效果 |
| :--- | :---: | :--- |
| 正常 (Normal) | - | 預設狀態，正常行動。 |
| 石化 (Petrified) | 3 | 無法進行任何行動。 |
| 中毒 (Poisoned) | 3 | 每回合開始時損失 30 點 HP。 |
| 受到鼓舞 (Inspired) | 3 | 造成的每次傷害額外增加 50 點。 |

### 5. AI 決策
AI 角色依照內部的 `seed` 決定行動與目標：
- **選擇行動**：從「普通攻擊 + 技能清單」中選第 `seed % 行動數` 個，接著 `seed + 1`。
- **選擇目標**：需要 N 位目標時，依序選擇第 `(seed + i) % 候選數` 位（`i = 0 ~ N-1`），選完後 `seed + 1`。

---

## 🎯 設計需求（非功能性需求）

- **技能擴充性 (OCP)**：新增或刪除技能時，不需修改角色、軍隊與戰鬥流程等核心類別。
- **狀態擴充性 (OCP)**：新增異常狀態時，不需修改角色的行動流程。
- **決策者解耦**：英雄（玩家輸入）與 AI（演算法）共用同一套戰鬥流程，只在「選擇行動」與「選擇目標」上有所不同。
- **可驗證性**：輸出格式必須與題目提供的測資逐字一致。

---

## 🧩 設計架構

本題同時面對多道行為變動性 Forces，因此綜合運用了多個設計模式，每個模式各自解決一道 Force：

| Force（行為變動） | 採用的設計 | 對應類別 |
| :--- | :--- | :--- |
| 10 種技能的效果各不相同 | **策略模式 (Strategy)** ＋ **樣板方法 (Template Method)** | `ActionStrategy` 定義「`execute()` → `applyToTargets()` → `applyToTarget()`」的固定流程，`BasicAttack` 與各 `Skill` 子類別只實作差異的部分 |
| 技能的目標對象（敵方／友方／自己／全體）與數量各不相同 | **資料驅動的目標設定** | 每個行動以 `ActionTargetGroup(ActionTargetType, targetCount)` 宣告目標，`targetCount = -1` 代表全體；`RPG.getCandidateGroups()` 統一算出候選目標 |
| 角色在不同狀態下的行動、傷害、回合效果不同 | **狀態模式 (State)** | `State` 提供 `takeTurn()`、`effect()`、`damageBonus()` 等預設行為，`Petrified`、`Poisoned`、`Inspired` 只覆寫需要改變的部分 |
| 英雄與 AI 的決策方式不同 | **樣板方法 (Template Method)** ＋ **策略模式 (Strategy)** | `Role` 宣告抽象的 `getAction()` 與 `choose()`，`Hero` 讀取玩家輸入，`AI` 再委派給 `AIChooseStrategy`（目前實作為 `ChooseBySeed`） |
| 詛咒、召喚的效果要在「角色死亡」時才觸發 | **觀察者模式 (Observer)** | 角色死亡時 `notifyObservers()`，由 `CurseEffect`、`SummonEffect` 各自決定回復多少 HP |
| 一拳攻擊依目標狀況有多種傷害規則 | **責任鏈模式 (Chain of Responsibility)** | `HighHpDamage → InPoisonedDamage → InPetrifiedDamage → InInspiredDamage → InNormalDamage`，新增判定規則時只需在鏈上插入新的 `OnePunchHandler` |

---

## 📊 OOA / OOD 設計與 UML 架構

魔王題著重於自主發掘 Forces 與獨立設計，相關設計圖檔如下：

### 1. OOA 分析階段圖
* **檔案連結**：[OOA UML 圖檔](./docs/ooa_uml.png)
* **說明**：OOA 階段以字串表示行動類型 (`actionType`) 與狀態 (`state`)，並對照技能表與狀態表，察覺出以下 Forces：
  * **Force-BV（原始型）——技能**：`consumeMP()`、`getActionTargetCount()`、`onActionReceived()` 會依行動類型抽換成多種行為之一（如耗魔 50、耗魔 100），而且未來還會新增技能。
  * **Force-BV（狀態型）——狀態**：`Role` 的 `state` 處於不同狀態時，會引起多道行為上的變化。例如 `generateStateEffect()` 在正常與中毒狀態下不同，`takeTurn()` 在正常與石化狀態下也不同。
  * **Force-BV（原始型）——AI 決策**：未來會擴充新的 AI 決策實作。
  * **Force-BV（審議式）——死亡事件**：Slime 受到傷害而死亡時，詛咒者與召喚者都需要各自執行一些事情（詛咒者回復 HP、召喚者補血）。
  * **Force-BV（比對輸入型）——一拳攻擊**：執行一拳攻擊時，必須先解析目標的狀態類型，每種類型的處理方式都不同，未來也可能依目標的其他屬性做不同處理。
  * **擴充性 Force (OCP)**：擴充新的技能、AI 決策、狀態或判斷屬性時，都不必修改 RPG 模組的內部程式碼。
  * **Problem**：若讓 `Role` 依字串判斷行動類型與狀態，每多一個技能或狀態，就得回頭在 `Role` 的多個方法中增加 `if-else` 分支。

### 2. OOD 設計階段圖
* **檔案連結**：[OOD UML 圖檔](./docs/ood_uml.png)
* **說明**：針對每一道 Force 套用對應的設計模式（詳見上方「設計架構」表格）：
  * **技能**：以 `ActionStrategy` 取代 `actionType` 字串。`BasicAttack` 與 10 個 `Skill` 子類別各自宣告 `mpCost` 與 `ActionTargetGroup`，並實作 `applyToTarget()`；召喚、自爆則額外覆寫 `execute()`。
  * **狀態**：以 `State` 取代 `state` 字串。`Inspired` 覆寫 `damageBonus()`，`Poisoned` 覆寫 `effect()`，`Petrified` 覆寫 `takeTurn()`。
  * **AI 決策**：`AI` 持有 `AIChooseStrategy` 介面，目前由 `ChooseBySeed` 實作。
  * **死亡事件**：`Role` 死亡時通知已註冊的 `Observer`，由 `CurseEffect`、`SummonEffect` 各自處理後續效果。
  * **一拳攻擊**：`OnePunch` 持有一條 `OnePunchHandler` 責任鏈，依序比對 `HighHpDamage → InPoisonedDamage → InPetrifiedDamage → InInspiredDamage → InNormalDamage`。
  * **目標選擇**：`RPG.getCandidateGroups()` 依 `ActionTargetType`（`ALLY` / `ENEMY` / `SELF`）篩出候選目標，再交由 `Role.choose()` 決定最終目標。

### 3. 系統循序圖 (Sequence Diagram)
* **檔案連結**：[循序圖圖檔](./docs/ood_sequence_diagram.png)
* **說明**：
  * 描繪 `RPG.start()` 之後的戰鬥主迴圈：在其中一方軍隊被殲滅前，`RPG` 反覆呼叫 `nextRound()`，依序讓每支 `Troop` 執行 `battle()`，再讓軍隊中的每位 `Role` 執行 `takeTurn()`。
  * 在 `takeTurn()` 中，角色會反覆選擇行動，直到 MP 足夠支付消耗為止；接著決定目標數量與目標、扣除 MP 並執行行動。
  * 以 `alt` 區塊區分三種情況：召喚時建立 `Slime` 並加入己方 `Troop`；自爆時施放者呼叫 `dead()`；其他行動則對目標 `Role` 呼叫 `onActionReceived()`。
  * 此循序圖沿用 OOA 階段以 `actionType` 字串分派行為的寫法。在 OOD 實作中，這些分支已分別由各技能的 `execute()` / `applyToTarget()` 取代。

---

## 📥 / 📤 輸入與輸出

### 軍隊設定格式
```text
#軍隊-1-開始
英雄 500 500 100 水球 火球 自我治療 鼓舞 召喚
WaterTA 300 300 70 水球 自我治療
#軍隊-1-結束
#軍隊-2-開始
Slime1 200 90 50
魔王 1000 500 60 詛咒 石化 下毒 火球
#軍隊-2-結束
```
- 每一行角色的格式為：`名稱 HP MP STR [技能...]`。
- `軍隊-1` 中必須有一位名為「英雄」的角色。
- `Main` 可以接受一個設定檔路徑作為參數；未提供時使用內建的預設軍隊。

### 玩家操作
- **選擇行動**：輸入行動編號（`0` 為普通攻擊，其後依序為各技能）。
- **選擇目標**：輸入目標編號，多個目標以逗號分隔（例如 `0, 1, 2`）。

### 測資
`src/test/resources/` 中有 10 組 `.in` / `.out` 測資，分別對應各技能情境：

`only-basic-attack`、`waterball-and-fireball-1v2`、`self-healing`、`petrochemical`、`poison`、`summon`、`self-explosion`、`cheerup`、`curse`、`one-punch`

目前 `MainTest` 會自動驗證 `waterball-and-fireball-1v2` 這組測資，可用以下指令執行：
```bash
mvn test
```
