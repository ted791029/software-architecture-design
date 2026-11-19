# 門面模式——處方診斷系統

## 📌 系統簡介
本專案為醫院 **處方診斷系統 (Prescriber System)** 的核心模組。模組用戶輸入病患的身分證字號與症狀，診斷器 (Prescriber) 就會依照處方診斷規則開出治療處方 (Prescription)，並把這次診斷存成病患的一筆病例 (Case)。

核心模組同時面對兩種使用者：
- **核心模組維護者**：需要直接操作 `PatientDatabase` 與 `Prescriber`，並能從外部擴充新的診斷規則。
- **模組用戶 (Client)**：只想用 1～3 行程式完成一次完整的診斷流程，不想了解模組內部的眾多類別。

本專案以 **門面模式 (Facade Pattern)** 提供 `PrescriptionDemandFacade`，把讀取資料檔、排隊診斷、建立病例、匯出結果等步驟收斂成單一入口；並以 **責任鏈模式 (Chain of Responsibility)** 實作可擴充的處方診斷規則。

---

## 🛠️ 核心規則與系統功能

### 1. 病患資料 (Patient)
| 欄位 | 規則 |
| :--- | :--- |
| 身分證字號 (id) | 開頭為大寫英文字母，之後為 9 位數字 |
| 姓名 (name) | 長度 1～30 |
| 性別 (gender) | `MALE` 或 `FEMALE` |
| 年紀 (age) | 1～180 的整數 |
| 身高 (height) | 1～500 的浮點數（公分） |
| 體重 (weight) | 1～500 的浮點數（公斤） |
| 病例 (cases) | 病患在醫院中的所有看診紀錄 |

### 2. 診斷流程
1. 用戶向診斷器提供病患的身分證字號與多項症狀，提出診斷要求。
2. 診斷器一次只診斷一位病患，新的要求會排到隊列後方，依先後順序處理；隊列清空時就停下來等待新的要求。
3. 每次診斷耗時 **3 秒**。
4. 診斷完成後，診斷器通知用戶診斷結果（一道處方）。
5. 用戶將這次的症狀、處方與診斷時間存成病患的一筆病例。

每道處方包含：名字 (name)、潛在疾病 (potential disease)、食用藥物 (medicines)、使用方法 (usage)。

### 3. 處方診斷規則
| 潛在疾病 | 診斷條件 | 處方名字 | 食用藥物 | 使用方法 |
| :--- | :--- | :--- | :--- | :--- |
| 新冠肺炎 (COVID-19) | 打噴嚏、頭痛、咳嗽 | 清冠一號 | 清冠一號 | 將相關藥材裝入茶包裡，使用 500 mL 溫、熱水沖泡悶煮 1～3 分鐘後即可飲用。 |
| 有人想你了 (Attractive) | 正值 18 歲的女性，且打噴嚏 | 青春抑制劑 | 假鬢角、臭味 | 把假鬢角黏在臉的兩側，讓自己異性緣差一點，自然就不會有人想妳了。 |
| 睡眠呼吸中止症 (SleepApneaSyndrome) | BMI 大於 26，且打呼 | 打呼抑制劑 | 一捲膠帶 | 睡覺時，撕下兩塊膠帶，將兩塊膠帶交錯黏在關閉的嘴巴上，就不會打呼了。 |

---

## 🎯 設計需求（非功能性需求）

### 面向核心模組維護者
- 將病患資料庫與診斷器封裝成 `PatientDatabase` 與 `Prescriber` 兩個類別，分別用來查詢、更新病患資料，以及要求診斷。
- 能在外部決定收到診斷結果時要觸發什麼行為，不用修改模組內部程式。
- 能在外部擴充新的處方診斷規則，不用修改模組內部程式。

### 面向模組用戶
- 以一個 **JSON 檔案** 決定病患資料庫中的所有病患資料。
- 以一個 **純文字檔案** 決定診斷器要支援哪些疾病，每一行為一個疾病名稱。
- 只要 **1～3 行程式** 就能執行一次完整的診斷流程，並能在這幾行中決定：病患資料檔、支援疾病檔、病患與症狀，以及診斷結果要匯出到哪個檔案、存成 JSON 還是 CSV。

---

## 🧩 設計架構

| Force | 採用的設計 | 對應類別 |
| :--- | :--- | :--- |
| 用戶為了完成一次診斷，必須了解許多類別與介面，而且彼此關聯複雜 | **門面模式 (Facade)** | `PrescriptionDemandFacade.request()` 依需求依序完成：更新病患資料庫、更新支援疾病、要求診斷、匯出病例 |
| 一次請求的參數組合多變（是否更新資料庫、是否更新支援疾病、匯出格式…） | **建造者模式 (Builder)** | `PrescriptionDemandRequest.Builder`：`updateDatabase()`、`updateSupportedDiseases()`、`prescriptionDemand()`、`export()` |
| 診斷時要依病患屬性與症狀逐一比對規則，且未來會持續新增規則 | **責任鏈模式 (Chain of Responsibility)** | `DiseasesHandler` 定義 `handle()` 流程，`Covid19Handler → AttractiveHandler → SleepApneaSyndromeHandler` 依序比對，新增規則只需加入新的 handler |
| 診斷器一次只能處理一位病患，但可能有多位用戶同時提出要求 | **生產者－消費者** | `PrescriberSystem` 把 `PrescriptionDemandInfo` 放進 `BlockingQueue`；`Prescriber` 在獨立執行緒中依序取出、診斷，再以 `CompletableFuture` 回傳處方 |

**分層：**
- **核心模組（`lib` 套件）**：`PrescriberSystem`、`PatientDatabase`、`Prescriber`、`DiseasesHandler`、`Patient`、`Case`、`Prescription` 等，供維護者直接使用與擴充。
- **門面層**：`PrescriptionDemandFacade` 與 `PrescriptionDemandRequest`，負責讀取 JSON / TXT 檔案、組裝責任鏈、啟動診斷器執行緒，以及把病例匯出成 JSON（Gson）或 CSV（Super CSV）。

**支援疾病的切換：** 每個 handler 都有 `isEnable` 旗標。讀取支援疾病檔後，`Prescriber.update()` 會先停用所有 handler，再啟用名稱出現在檔案中的 handler，因此不用重新組裝責任鏈就能改變支援的疾病。

---

## 📊 OOA / OOD 設計與 UML 架構

### 1. OOA 分析階段圖
* **檔案連結**：[OOA UML 圖檔](./docs/ooa_uml.png)
* **說明**：
  * **Force-易用性**：為了簡化 `PrescriberSystem` 的使用方式，Client 為了完成診斷，必須了解多個類別與介面。
  * **Force-高結構複雜度**：`PrescriberSystem` 中存在許多類別與介面，類別之間的關聯錯綜複雜，組成也不規律。
  * **Force-BV（比對輸入型）**：執行 `prescriptionDemand` 時，系統要先解析病患的屬性，每種情況的處理方式不同（例如開出清冠一號或青春抑制劑），而且未來會持續擴充症狀的種類。
  * **Force-擴充性**：維護者希望能在外部擴充新的處方診斷規則，而不用修改模組內部程式。
  * **Problem**：若讓 Client 直接操作所有內部類別，並在 `Prescriber` 中以 `if-else` 判斷每一種疾病，Client 的使用成本會很高，新增診斷規則時也必須修改模組內部。

### 2. OOD 設計階段圖
* **檔案連結**：[OOD UML 圖檔](./docs/ood_uml.png)
* **說明**：
  * 導入 **Facade Pattern**：新增 `PrescriptionDemandFacade`（圖中綠色），Client 只需透過 `PrescriptionDemandRequestBuilder` 組出請求，再呼叫 `request()`。
  * 導入 **Chain of Responsibility**：以抽象類別 `DiseasesHandler` 搭配 `next` 關聯，串起 `Covid19Handler`、`AttractiveHandler`、`SleepApneaSyndromeHandler`，各自實作 `match()` 與 `handling()`。
  * `Disease` 保存疾病名稱與對應症狀，提供 `isMatchDiseaseSymptoms()` 供各 handler 比對。
  * `Prescriber` 以子執行緒不斷從隊列取出 `PrescriptionDemandInfo` 並診斷。
* **歷史紀錄**：設計過程中的舊版 OOD 圖保存在 [`docs/歷史紀錄/`](./docs/歷史紀錄/)（[v1](./docs/歷史紀錄/OOD_v1.png)、[v2](./docs/歷史紀錄/OOD_v2.png)）。

### 3. 系統循序圖 (Sequence Diagram)
* **檔案連結**：[循序圖圖檔](./docs/ood_sequence_diagram.png)
* **說明**：描繪 `PrescriberSystem` 的三個操作：
  1. `updateDatabase()`：呼叫 `PatientDatabase.updateAll()` 更新所有病患資料。
  2. `updateSupportedDiseases()`：呼叫 `Prescriber.update()` 更新支援的疾病。
  3. `prescriptionDemand()`：先以 `findPatient()` 找出病患，交給 `Prescriber` 診斷取得處方，再建立 `Case`、呼叫 `Patient.addCase()`，最後以 `PatientDatabase.save()` 存回。

---

## ▶️ 執行方式

Client 只需建立門面，並以 Builder 組出請求，即可完成一次完整的診斷流程：

```java
PrescriptionDemandFacade facade = new PrescriptionDemandFacade();

facade.request(new PrescriptionDemandRequest.Builder()
        .updateDatabase("database.json")                      // 病患資料檔（可省略）
        .updateSupportedDiseases("supportedDiseases.txt")     // 支援疾病檔（可省略）
        .prescriptionDemand("P208317507", List.of("sneeze"))  // 病患與症狀
        .export("P208317507_病例", ExportType.JSON)            // 匯出檔名與格式
        .build());
```

- 建立門面時會先載入預設的病患資料 `default_database.json`。
- 資料檔放在 [`src/main/java/com/ted/app/file/`](./src/main/java/com/ted/app/file/) 下：
  - `database/`：病患資料 JSON。
  - `disease/`：支援疾病清單 TXT。
  - `cases/`：匯出的病例。
- `Main` 中示範了三種情境：單純診斷、更新支援疾病後診斷、更新病患資料庫後診斷。
