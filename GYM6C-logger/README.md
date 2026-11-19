# 複合模式——日誌框架

## 📌 系統簡介
本專案為一個簡易的 **日誌框架 (Logging Framework)**。開發者可以定義多個具有父子階層的日誌器 (Logger)，在程式任何地方以名稱取得日誌器，並撰寫不同分級 (Level) 的日誌訊息。每個日誌器可以設定分級門檻、輸出器與訊息佈置，子日誌器沒有設定的部分會繼承父日誌器。

本專案以 **複合模式 (Composite Pattern)** 實作「複合輸出器」，讓多個輸出器可以任意巢狀組合；並以 **策略模式 (Strategy Pattern)** 抽換訊息佈置。這樣一來，擴充新的輸出器或訊息佈置時都不需要修改日誌器本身。

---

## 🛠️ 核心規則與系統功能

### 1. 訊息分級 (Level)
由小到大依序為：

`TRACE` < `INFO` < `DEBUG` < `WARN` < `ERROR`

日誌器提供 `trace()`、`info()`、`debug()`、`warn()`、`error()` 五個方法，分別撰寫對應分級的訊息。

### 2. 日誌器配置
| 參數 | 說明 |
| :--- | :--- |
| **名稱 (Name)** | 每個日誌器都有唯一的名稱，根日誌器的名稱必為 `Root`。 |
| **父日誌器 (Parent)** | 除了根日誌器以外都要指定父日誌器；沒有覆寫的設定會繼承父日誌器。 |
| **分級門檻 (Level Threshold)** | 請求撰寫的分級必須**大於或等於**門檻才會受理。例如門檻為 `DEBUG` 時，只受理 `DEBUG`、`WARN`、`ERROR`。 |
| **輸出器 (Exporter)** | 訊息通過門檻後，交給輸出器輸出。 |
| **訊息佈置 (Layout)** | 決定輸出訊息的排版方式。 |

根日誌器必須設定好分級門檻、輸出器與訊息佈置；子日誌器只需設定要覆寫的部分。

### 3. 輸出器 (Exporter)
| 輸出器 | 說明 |
| :--- | :--- |
| **控制台輸出器 (ConsoleExporter)** | 將訊息輸出到標準輸出。 |
| **檔案輸出器 (FileExporter)** | 將訊息附加 (Append) 到指定的檔案中。 |
| **複合輸出器 (CompositeExporter)** | 依序交給多個子輸出器輸出；子輸出器也可以是另一個複合輸出器。 |

### 4. 訊息佈置 (Layout)
目前支援 **標準日誌排版 (StandardLayout)**：

```text
時間 ├訊息分級 日誌器名稱 - 訊息內容
2026-11-19 10:30:15.123 ├INFO app.game - The game begins.
```

時間格式為 `yyyy-MM-dd HH:mm:ss.SSS`。

---

## 🎯 設計需求（非功能性需求）

- **輸出器擴充性 (OCP)**：擴充輸出器的種類時，無需修改既有程式碼。
- **訊息佈置擴充性 (OCP)**：擴充訊息佈置的種類時，無需修改既有程式碼。

---

## 🧩 設計架構

| Force | 採用的設計 | 對應類別 |
| :--- | :--- | :--- |
| 輸出器之間存在巢狀的「整體－部分」樹狀結構，且未來會新增更多種輸出器 | **複合模式 (Composite)** | `Exporter` 介面；`ConsoleExporter`、`FileExporter` 為葉節點，`CompositeExporter` 持有多個 `Exporter` 並依序委派 `export()` |
| 訊息佈置可以抽換，而且未來會持續新增 | **策略模式 (Strategy)** | `LayoutStrategy` 介面，目前由 `StandardLayout` 實作 `format(Message)` |
| 日誌器有多個可選參數，且建構時有一定的規則（根日誌器三項設定必填、子日誌器必須指定父日誌器） | **建造者模式 (Builder)** | `Logger.Builder`：`root(...)` 建立根日誌器，`build(name, parent)` 建立子日誌器，沒設定的參數自動沿用父日誌器的設定 |
| 開發者要能在程式任意處以名稱取得日誌器 | **集中註冊** | `LogManager` 以 `Map<String, Logger>` 保存所有日誌器，提供靜態方法 `getLogger(name)` |

`Logger` 本身只依賴 `Exporter`、`LayoutStrategy` 與 `LevelThreshold` 三個抽象，撰寫訊息時的流程固定為：
1. 以 `LevelThreshold.isPass(level)` 判斷是否受理。
2. 建立 `Message`，交給 `LayoutStrategy.format()` 排版。
3. 交給 `Exporter.export()` 輸出。

---

## ⚙️ 設定檔

除了用 `Logger.Builder` 在程式中建立日誌器，也可以透過 `LogManager.loadConfig()` 讀取 JSON 設定檔，由 `LoggerConfigurator`（使用 Gson 解析）建立整棵日誌器樹。

[`src/main/resources/setting.json`](./src/main/resources/setting.json) 的結構如下：最外層為根日誌器；除了 `levelThreshold`、`exporter`、`layout` 以外的 key，會被視為子日誌器的名稱。

```json
{
  "loggers": {
    "levelThreshold": "DEBUG",
    "exporter": { "type": "console" },
    "layout": "standard",
    "app.game": {
      "levelThreshold": "INFO",
      "exporter": {
        "type": "composite",
        "children": [
          { "type": "console" },
          {
            "type": "composite",
            "children": [
              { "type": "file", "fileName": "game.log" },
              { "type": "file", "fileName": "game.backup.log" }
            ]
          }
        ]
      },
      "app.game.ai": { "levelThreshold": "TRACE" }
    }
  }
}
```

| 日誌器 | 分級門檻 | 輸出器 | 訊息佈置 |
| :--- | :--- | :--- | :--- |
| `Root` | `DEBUG` | 控制台 | 標準 |
| `app.game` | `INFO` | 控制台 ＋（`game.log` ＋ `game.backup.log`） | 繼承 `Root` |
| `app.game.ai` | `TRACE` | 繼承 `app.game` | 繼承 `app.game` |

---

## 📊 OOA / OOD 設計與 UML 架構

### 1. OOA 分析階段圖
* **檔案連結**：[OOA UML 圖檔](./docs/ooa_uml.png)
* **說明**：
  * **Force-樹狀結構**：輸出器之間至少存在一個遞迴關聯，而且是嚴格的階層，不能出現循環，每個物件最多只有一個父親。
  * **Force-結構變動性**：結構中組成的類別以及類別之間的關係可能會新增或改變，例如未來會有更多種輸出器。
  * **Force-擴充性 (OCP)**：未來擴充不同種類的輸出器時，不必修改 Client 所依賴的既有程式碼。
  * **Force-BV（原始型）**：希望能抽換 `format` 行為，而且未來還會持續新增。
  * **Force-擴充性 (OCP)**：擴充新的 `format` 時，不需修改 `Logger`。
  * **Force-建構規則性**：建構物件時有一定的規則，例如根日誌器一定要有分級門檻、訊息佈置與輸出器，子日誌器一定要有父日誌器。
  * **Force-建構變動性**：物件有多個屬性，但每次建構時的組合都不同。
  * **Problem**：若在 `Logger` 中以 `if-else` 判斷輸出器種類與排版方式，並以多個建構子處理各種參數組合，每新增一種輸出器或排版都必須修改 `Logger`。

### 2. OOD 設計階段圖
* **檔案連結**：[OOD UML 圖檔](./docs/ood_uml.png)
* **說明**：
  * 導入 **Composite Pattern**：定義 `Export` 介面，`ConsoleExporter`、`FileExporter`、`CompositeExporter` 皆實作此介面（圖中綠色），`CompositeExporter` 再以 `1..*` 關聯持有多個 `Export`，形成可遞迴組合的樹狀結構。
  * 導入 **Strategy Pattern**：定義 `LayoutStrategy` 介面，由 `StandardLayout` 實作，`Logger` 只依賴介面。
  * 導入 **Builder**：以 `Builder` 封裝日誌器的建構規則與參數組合。
  * `LoggerManager`（程式中為 `LogManager`）負責註冊與查詢日誌器，`LoggerConfigurator` 負責將設定檔轉換成日誌器。

### 3. 系統循序圖 (Sequence Diagram)
* **檔案連結**：[循序圖圖檔](./docs/ood_sequence_diagram.png)
* **說明**：
  * 描繪應用範例中 `Game.start()` 的執行過程：`Game` 先以 `info()` 記錄遊戲開始，接著對每位 `AI` 以 `trace()` 記錄回合開始、呼叫 `makeDecision()`，再以 `trace()` 記錄回合結束，最後以 `debug()` 記錄遊戲結束。
  * `AI.makeDecision()` 中再依序呼叫 `trace()`、`warn()`、`error()`、`trace()`。各訊息是否輸出，由對應日誌器的分級門檻決定。

---

## ▶️ 執行方式

`Main` 會讀取 `setting.json` 建立日誌器並註冊到 `LogManager`，再執行應用範例：
```java
List<Logger> loggers = loadConfig("setting.json");
declareRootLogger(loggers);
new Game().start();
```

`Game` 使用 `app.game` 日誌器，`AI` 使用 `app.game.ai` 日誌器：
- `app.game` 的門檻為 `INFO`，所以 `Game` 中的 `trace()` 不會輸出，`info()` 與 `debug()` 會輸出。
- `app.game.ai` 的門檻為 `TRACE`，所以 `AI` 的所有訊息都會輸出。
- 兩者的輸出器都是「控制台 ＋ 兩個檔案」，所以訊息會同時印在控制台，並附加到 `game.log` 與 `game.backup.log`。
