package com.ted.app;

import com.ted.app.role.Hero;
import com.ted.app.role.Role;
import com.ted.app.role.ai.AI;
import com.ted.app.strategy.action.skill.*;
import com.ted.app.strategy.aiChoose.ChooseBySeed;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 遊戲入口。
 * <p>
 * 用法：
 * <ul>
 *     <li>不帶參數：使用內建的預設隊伍開始遊戲</li>
 *     <li>帶一個檔案路徑參數：從檔案讀取隊伍設定（格式同 src/test/resources/*.in，只讀取「#軍隊-X-開始 ~ #軍隊-X-結束」區段）</li>
 * </ul>
 * 每位角色一行：名稱 HP MP STR [技能...]，名為「英雄」的角色由玩家操作，其餘由 AI 操作。
 */
public class Main {

    private static final String HERO_NAME = "英雄";

    private static final List<String> DEFAULT_CONFIG = List.of(
            "#軍隊-1-開始",
            "英雄 500 500 100 水球 火球 自我治療 鼓舞 召喚",
            "WaterTA 300 300 70 水球 自我治療",
            "#軍隊-1-結束",
            "#軍隊-2-開始",
            "Slime1 200 90 50",
            "Slime2 200 90 50",
            "魔王 1000 500 60 詛咒 石化 下毒 火球",
            "#軍隊-2-結束"
    );

    public static void main(String[] args) throws IOException {
        List<String> config = args.length > 0
                ? Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8)
                : DEFAULT_CONFIG;

        RPG rpg = new RPG();
        List<Troop> troops = loadTroops(config, rpg);

        if (troops.size() != 2) {
            throw new IllegalArgumentException("需要剛好兩支軍隊，目前為 " + troops.size() + " 支");
        }

        rpg.setTroops(troops);
        rpg.setHero(findHero(troops.get(0)));
        rpg.setTroop2(troops.get(1));

        printGuide();
        rpg.start();
    }

    private static Role findHero(Troop troop) {
        for (Role role : troop.getRoles()) {
            if (role instanceof Hero) {
                return role;
            }
        }
        throw new IllegalArgumentException("軍隊-1 中必須有一位名為「" + HERO_NAME + "」的角色");
    }

    private static List<Troop> loadTroops(List<String> lines, RPG rpg) {
        List<Troop> troops = new ArrayList<>();
        Troop currentTroop = null;

        for (String raw : lines) {
            String line = raw.trim();

            if (line.startsWith("#軍隊-") && line.endsWith("-開始")) {
                String id = line.substring(4, line.length() - 3);
                currentTroop = new Troop(id, rpg);
                troops.add(currentTroop);
                continue;
            }

            if (line.startsWith("#軍隊-") && line.endsWith("-結束")) {
                currentTroop = null;
                continue;
            }

            // 軍隊區段外的內容（例如測試檔中的預錄輸入）一律忽略，改由玩家即時輸入
            if (currentTroop == null || line.isEmpty()) {
                continue;
            }

            currentTroop.add(parseRole(line, rpg, currentTroop));
        }

        return troops;
    }

    private static Role parseRole(String line, RPG rpg, Troop troop) {
        String[] parts = line.split("\\s+");

        if (parts.length < 4) {
            throw new IllegalArgumentException("角色格式錯誤（名稱 HP MP STR [技能...]）：" + line);
        }

        String name = parts[0];
        int hp = Integer.parseInt(parts[1]);
        int mp = Integer.parseInt(parts[2]);
        int str = Integer.parseInt(parts[3]);

        List<Skill> skills = new ArrayList<>();
        for (int i = 4; i < parts.length; i++) {
            skills.add(createSkill(parts[i]));
        }

        if (name.equals(HERO_NAME)) {
            return new Hero(hp, name, mp, rpg, str, skills, troop);
        }
        return new AI(hp, name, mp, rpg, str, skills, troop, new ChooseBySeed());
    }

    private static Skill createSkill(String skillName) {
        return switch (skillName) {
            case "水球" -> new WaterBall();
            case "火球" -> new FireBall();
            case "自我治療" -> new SelfHealing();
            case "石化" -> new Petrochemical();
            case "下毒" -> new Poison();
            case "召喚" -> new Summon();
            case "自爆" -> new SelfExplosion();
            case "鼓舞" -> new CheerUp();
            case "詛咒" -> new Curse();
            case "一拳攻擊" -> new OnePunch();
            default -> throw new IllegalArgumentException("未知的技能：" + skillName);
        };
    }

    private static void printGuide() {
        System.out.println("===== RPG 開始 =====");
        System.out.println("輪到英雄時：輸入行動編號；需要選目標時輸入目標編號，多個目標以逗號分隔（例如 0,1）。");
        System.out.println();
    }
}
