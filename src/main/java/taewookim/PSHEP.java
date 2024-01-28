package taewookim;

import org.CatAndDomi.CatFish;
import org.CatAndDomi.components.CatFishBuilder;
import org.CatAndDomi.components.ComponentType;
import org.CatAndDomi.components.command.ArgsTypes;
import org.CatAndDomi.components.command.CommandComponent;
import org.CatAndDomi.components.message.MessageComponent;
import org.CatAndDomi.components.pageinventory.PageInventoryComponent;
import org.CatAndDomi.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.gang.pogoinscoreboard.PogoinScoreboard;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class PSHEP extends JavaPlugin implements Listener {

    public static CatFish cf;
    public static MessageComponent mc;
    public static CommandComponent cc;
    public static PageInventoryComponent pic;
    public static Map<Material, Integer> sellprice = new HashMap<>();
    public static Random r;

    public static int price = 0;
    public static int max = 320;
    public static int startmax = 320;
    public static int maxtomax = 1280;
    public static boolean isGameing = false;

    public static int count = 20;
    public static int realcount = 0;
    public static boolean iscount = false;
    public static boolean isskip = false;

    public static int roulette_start = 20;
    public static int roulette_ing = 50;
    public static int roulette_final = 20;
    public static PogoinScoreboard scoreboard;

    public static ArrayList<Roulette> roulettes = new ArrayList<>();
    public BossBar bar;
    public BossBar bar1;

    public ArrayList<Roulette> getRoulettes() {
        return roulettes;
    }

    public static Map<String, ShepherdArea> areamap = new HashMap<>();
    public static Map<Player, AreaSetter> settermap = new HashMap<>();

    public static boolean deathsound = false;

    public static void Title(String string, String string1) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(string, string1);
        }
    }

    public static void Title(String string, String string1, int i, int i1, int i2) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(string, string1, i, i1, i2);
        }
    }

    public static void Sound(Sound sound, float f, float f1) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, sound, f, f1);
        }
    }

    public void Update() {
        BukkitRunnable brun = new BukkitRunnable() {
            @Override
            public void run() {
                if(roulettes.size()!=0) {
                    roulettes.get(0).Update();
                    if(roulettes.get(0).isEnd) {
                        roulettes.remove(0);
                        scoreboard.increaseWool(roulettes.size());
                    }
                }
                double d = ((double)price)/((double)max);
                bar.setProgress(d<1?d:1);
                bar.setTitle(isGameing?mc.getString("BossbarName").replace("<COUNT>", price+"").replace("<MAX>", max+""):mc.getString("게임준비중"));
                int count = 0;
                for(Map.Entry<String, ShepherdArea> entry : areamap.entrySet()) {
                    count+=entry.getValue().getSheeps();
                }
                bar1.setTitle(mc.getString("Bossbar1Name").replace("<COUNT>", count+""));
            }
        };brun.runTaskTimer(this, 0, 0);
        BukkitRunnable brun1 = new BukkitRunnable() {
            @Override
            public void run() {
                if(isGameing) {
                    if(max<=price&&roulettes.isEmpty()) {
                        if(iscount) {
                            realcount++;
                            if(count<realcount) {
                                gamereset(null);
                                isGameing = false;
                                Title(mc.getString("승리타이틀1"), mc.getString("승리타이틀2"));
                                Sound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                            }else {
                                Title(mc.getString("카운트다운타이틀1").replace("<COUNT>", ((int)count-realcount+1)+""), mc.getString("카운트다운타이틀2").replace("<COUNT>", ((int)count-realcount+1)+""));
                                Sound(Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                            }
                        }else {
                            iscount = true;
                        }
                    }else {
                        iscount = false;
                        realcount = 0;
                    }
                }
                if(deathsound) {
                    deathsound = false;
                    addcount(200, false);
                    Sound(Sound.BLOCK_END_PORTAL_SPAWN, 100, 0);
                    Sound(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 100, 0);
                }
            }
        };brun1.runTaskTimer(this, 20, 20);
    }

    public boolean setupCatFish() {
        try{
            if(Bukkit.getPluginManager().isPluginEnabled("CatFish")) {
                cf = (CatFish) Bukkit.getPluginManager().getPlugin("CatFish");
                return true;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void loadConfig2() {
        File file = new File(getDataFolder()+"/areadata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(config.isSet("arealist")) {
            for(String string : config.getStringList("arealist")) {
                File file1 = new File(getDataFolder()+"/areadata/"+string+".yml");
                YamlConfiguration config1 = YamlConfiguration.loadConfiguration(file1);
                areamap.put(string, new ShepherdArea(config1));
            }
        }
    }

    public void loadConfig1() {
        File file = new File(getDataFolder()+"/config - gamesetting.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(!config.isSet("max")) {
            config.set("max", 320);
        }
        if(!config.isSet("startmax")) {
            config.set("startmax", 320);
        }
        if(!config.isSet("maxtomax")) {
            config.set("maxtomax", 1280);
        }
        if(!config.isSet("price")) {
            config.set("price", 0);
        }
        if(!config.isSet("count")) {
            config.set("count", 20);
        }
        max = config.getInt("max");
        maxtomax = config.getInt("maxtomax");
        price = config.getInt("price");
        count = config.getInt("count");
        startmax = config.getInt("startmax");
        try{
            config.save(file);
        }catch(Exception e) {
        }
    }

    public void loadConfig() {
        File file = new File(getDataFolder()+"/config - sellprice.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(!config.isSet(Material.WHITE_WOOL.toString())) {
            config.set(Material.WHITE_WOOL.toString(), 1);
        }
        if(!config.isSet(Material.LIGHT_GRAY_WOOL.toString())) {
            config.set(Material.LIGHT_GRAY_WOOL.toString(), 3);
        }
        if(!config.isSet(Material.GRAY_WOOL.toString())) {
            config.set(Material.GRAY_WOOL.toString(), 3);
        }
        if(!config.isSet(Material.BLACK_WOOL.toString())) {
            config.set(Material.BLACK_WOOL.toString(), 3);
        }
        if(!config.isSet(Material.BROWN_WOOL.toString())) {
            config.set(Material.BROWN_WOOL.toString(), 5);
        }
        if(!config.isSet(Material.PINK_WOOL.toString())) {
            config.set(Material.PINK_WOOL.toString(), 20);
        }
        for(String key : config.getKeys(true)) {
            sellprice.put(Material.valueOf(key), config.getInt(key));
        }
        try{
            config.save(file);
        }catch(Exception e) {
        }
    }

    public void save() {
        File file = new File(getDataFolder()+"/config - gamesetting.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("price", price);
        config.set("max", max);
        try{
            config.save(file);
        }catch(Exception e) {
        }
        //save2
        ArrayList<String> arealist = new ArrayList<>();
        for(Map.Entry<String, ShepherdArea> entry : areamap.entrySet()) {
            arealist.add(entry.getKey());
            file = new File(getDataFolder()+"/areadata/"+entry.getKey()+".yml");
            config = YamlConfiguration.loadConfiguration(file);
            entry.getValue().save(config);
            try{
                config.save(file);
            }catch(Exception e) {
            }
        }
        file = new File(getDataFolder()+"/areadata.yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.set("arealist", arealist);
        try{
            config.save(file);
        }catch(Exception e) {
        }
    }

    public static void addcount(int a, boolean b) {
        if(max+a>maxtomax) {
            int i = a-(maxtomax-max);
            if(b) {
                PSHEP.Title(PSHEP.mc.getString("양털감소타이틀1").replace("<NUM>", i+""), PSHEP.mc.getString("양털감소타이틀2").replace("<NUM>", i+""), 0, 20, 10);
            }
            price-=i;
            max = maxtomax;
            if(price<0) {
                price=0;
            }
        }else {
            max+=a;
            if(b) {
                PSHEP.Title(PSHEP.mc.getString("양털증가타이틀3").replace("<NUM>", a+""), PSHEP.mc.getString("양털증가타이틀4").replace("<NUM>", a+""), 0, 20, 10);
            }
        }
        if(!b) {
            PSHEP.Title(PSHEP.mc.getString("사망타이틀").replace("<COUNT>", a+""), PSHEP.mc.getString("사망타이틀1").replace("<COUNT>", a+""), 0, 20, 10);
        }
    }

    public static void giveeggs(int i) {
        ItemStack it = new ItemStack(Material.SHEEP_SPAWN_EGG, i);
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), it)) {
                p.getInventory().addItem(it.clone());
            }else {
                p.getWorld().dropItem(p.getLocation(), it.clone());
            }
        }
    }

    public static void gamestart(CommandSender commandSender, String... strings) {
        isGameing = true;
        Title(mc.getString("시작타이틀1"), mc.getString("시작타이틀2"));
    }

    public static void gamestop(CommandSender commandSender, String... strings) {
        isGameing = false;
        Title(mc.getString("중지타이틀1"), mc.getString("중지타이틀2"));
    }

    public static void gamereset(CommandSender commandSender, String... strings) {
        Title(mc.getString("리셋타이틀1"), mc.getString("리셋타이틀2"));
        price = 0;
        max = startmax;
    }

    public static void opengui(CommandSender commandSender, String... strings) {
        if(isGameing) {
            if(commandSender instanceof Player p) {
                pic.getInventory("납품").openInventory(p, 0);
            }
        }else {
            commandSender.sendMessage(mc.getString("접두어") + mc.getString("게임시작false"));
        }
    }

    public static void increacewool(CommandSender commandSender, Integer i1, Integer i2, String... strings) {
        if(isGameing) {
            roulettes.add(new Roulette(r.nextInt(i2-i1)+i1));
            if(scoreboard!=null) {
                scoreboard.increaseWool(roulettes.size());
            }
        }else {
            commandSender.sendMessage(mc.getString("접두어") + mc.getString("게임시작false"));
        }
    }

    public static void setskip(CommandSender commandSender, String... strings) {
        isskip=!isskip;
        commandSender.sendMessage(mc.getString("접두어") + mc.getString("롤렛스킵설정").replace("<VAULE>", isskip+""));
    }

    public static void addDebuff(CommandSender commandSender, String... strings) {
        if(isGameing) {
            roulettes.add(new DebuffRoulette(10));
            if(scoreboard!=null) {
                scoreboard.increaseWool(roulettes.size());
            }
        }else {
            commandSender.sendMessage(mc.getString("접두어") + mc.getString("게임시작false"));
        }
    }

    public static void helpeggs(CommandSender commandSender, Integer i1, Integer i2, String... strings) {
        if(isGameing) {
            roulettes.add(new HelpRoulette(r.nextInt(i2-i1)+i1));
            if(scoreboard!=null) {
                scoreboard.increaseWool(roulettes.size());
            }
        }else {
            commandSender.sendMessage(mc.getString("접두어") + mc.getString("게임시작false"));
        }

    }

    public static void sheepareasetting(CommandSender commandSender, String name, String... strings) {
        if(commandSender instanceof Player p) {
            p.sendMessage(mc.getString("접두어") + mc.getString("영역설정").replace("<POS>", mc.getString("pos1")));
            settermap.put(p, new AreaSetter(name));
        }
    }

    public static void sheeparealist(CommandSender commandSender, String... strings) {
        commandSender.sendMessage(mc.getString("접두어")+mc.getString("양영역정보"));
        for(Map.Entry<String, ShepherdArea> entry : areamap.entrySet()) {
            Location loc = entry.getValue().mid;
            Vector size = entry.getValue().size;
            commandSender.sendMessage(mc.getString("접두어")+mc.getString("양영역정보1")
                    .replace("<NAME>", entry.getKey())
                    .replace("<LOC>", mc.getString("loc").replace("<X>", ((int)loc.getX())+"").replace("<Y>", ((int)loc.getY())+"").replace("<Z>", ((int)loc.getZ())+""))
                    .replace("<DX>", ((int)size.getX())+"")
                    .replace("<DY>", ((int)size.getY())+"")
                    .replace("<DZ>", ((int)size.getZ())+"")
            );
        }
    }

    public static void sheepareadelete(CommandSender commandSender, String name, String... strings) {
        areamap.remove(name);
        commandSender.sendMessage(mc.getString("접두어") + mc.getString("영역삭제완료"));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(!setupCatFish()) {
            System.out.println("CatFish플러그인을 로드하지 못했습니다. 플러그인을 종료합니다.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        loadConfig();
        loadConfig1();
        loadConfig2();
        r = new Random();
        new CatFishBuilder(this)
                .addComponents(ComponentType.COMMAND)
                .addComponents(ComponentType.PAGEINVENTORY)
                .addComponents(ComponentType.MESSAGE)
                .build();
        mc = (MessageComponent) cf.getComponent(this, ComponentType.MESSAGE);
        cc = (CommandComponent) cf.getComponent(this, ComponentType.COMMAND);
        pic = (PageInventoryComponent) cf.getComponent(this, ComponentType.PAGEINVENTORY);
        //메세지
        mc.addMessages("접두어", "&f[ &e양치기 타이쿤 &f] ")
                .addMessages("납품확인.이름", "&a&l확인")
                .addMessages("납품확인.아이템", Material.GREEN_WOOL.toString())
                .addMessages("납품확인.설명", Arrays.asList())
                .addMessages("납품취소.이름", "&c&l취소")
                .addMessages("납품취소.아이템", Material.RED_WOOL.toString())
                .addMessages("납품취소.설명", Arrays.asList())
                .addMessages("승리타이틀1", "&f[ &a승리 &f]")
                .addMessages("승리타이틀2", "&e게임에서 승리하여 WIN이 +1 올라갑니다.")
                .addMessages("카운트다운타이틀1", "&f[ &c카운트다운 &f]")
                .addMessages("카운트다운타이틀2", "&6<COUNT>초")
                .addMessages("양털증가타이틀1", "&a<NUM>개")
                .addMessages("양털증가타이틀2", "&7양털 증가 룰렛 돌아가는중..")
                .addMessages("양털증가타이틀3", "&a<NUM>개")
                .addMessages("양털증가타이틀4", "&7남은 양털이 추가되었습니다.")
                .addMessages("시작타이틀1", "&f[ &c안내 &f]")
                .addMessages("시작타이틀2", "&e양치기 타이쿤이 시작되었습니다.")
                .addMessages("중지타이틀1", "&f[ &c안내 &f]")
                .addMessages("중지타이틀2", "&e양치기 타이쿤이 중지되었습니다.")
                .addMessages("리셋타이틀1", "&f[ &c안내 &f]")
                .addMessages("리셋타이틀2", "&e양치기 타이쿤이 리셋되었습니다.")
                .addMessages("게임시작false", "아직 게임이 시작되지 않았습니다.")
                .addMessages("롤렛스킵설정", "롤렛스킵여부가 <VAULE>로 변경되었습니다.")
                .addMessages("알지급타이틀1", "&b<NUM>개")
                .addMessages("알지급타이틀2", "&7알지급 룰렛 돌아가는중..")
                .addMessages("알지급타이틀3", "&b<NUM>개")
                .addMessages("알지급타이틀4", "&7스폰알이 지급 되었습니다!")
                .addMessages("BossbarName", "남은 양털 개수 : <COUNT> / <MAX>개")
                .addMessages("납품완료", "양털을 &a&l<COUNT>&f&l개 납품하였습니다.")
                .addMessages("게임준비중", "게임 준비중")
                .addMessages("양털감소타이틀1", "&a<NUM>개")
                .addMessages("양털감소타이틀2", "&c최대 개수에 도달하여 이미 납품한 양털의 개수에서 차감됩니다.")
                .addMessages("Bossbar1Name", "현재 양 마리수 : <COUNT>")
                .addMessages("영역설정", "블럭에 좌클릭을 해서 <POS>를 설정해주세요.")
                .addMessages("pos1", "pos1")
                .addMessages("pos2", "pos2")
                .addMessages("영역설정완료", "<POS>를 <LOC>로 설정하였습니다.")
                .addMessages("loc", "(x:<X>, y:<Y>, z:<Z>)")
                .addMessages("영역생성완료", "<NAME>영역이 성공적으로 생성되었습니다.")
                .addMessages("양영역정보", "양영역정보:")
                .addMessages("양영역정보1", "<NAME>= 중심:<LOC>, 크기:(x:<DX>, y:<DY>, z:<DZ>)")
                .addMessages("영역삭제완료", "<NAME>영역이 성공적으로 삭제되었습니다.")
                .addMessages("사망타이틀", "&f[ &c사망하셨습니다. &f]")
                .addMessages("사망타이틀1", "&e사망패널티로 양털이 <COUNT>개가 추가로 증가됩니다!")
                .load();
        //커맨드
        try{
            cc.addCommand(
                    this.getClass().getMethod("gamestart", CommandSender.class, String[].class)
                    , new ArgsTypes[]{}, mc.getString("접두어") + "/게임 시작 - 게임을 시작합니다."
                    , "게임", "시작"
            );
            cc.addCommand(
                    this.getClass().getMethod("gamestop", CommandSender.class, String[].class)
                    , new ArgsTypes[]{}, mc.getString("접두어") + "/게임 중지 - 게임을 중지합니다."
                    , "게임", "중지"
            );
            cc.addCommand(
                    this.getClass().getMethod("gamereset", CommandSender.class, String[].class)
                    , new ArgsTypes[]{}, mc.getString("접두어") + "/게임 리셋 - 게임을 리셋합니다."
                    , "게임", "리셋"
            );
            cc.addCommand(
                    this.getClass().getMethod("opengui", CommandSender.class, String[].class)
                    , new ArgsTypes[]{}, mc.getString("접두어") + "/납품 - 납품하는 GUI를 엽니다."
                    , "납품"
            );
            cc.addCommand(
                    this.getClass().getMethod("increacewool", CommandSender.class, Integer.class, Integer.class, String[].class)
                    , new ArgsTypes[]{ArgsTypes.INTEGER, ArgsTypes.INTEGER}, mc.getString("접두어") + "/양털증가 <최소> <최대> - 지정된 값만큼 양털을 증가시키는 롤렛을 돌립니다."
                    , "양털증가"
            );
            cc.addCommand(
                    this.getClass().getMethod("setskip", CommandSender.class, String[].class)
                    , new ArgsTypes[]{}, mc.getString("접두어") + "/양털룰렛스킵 - 양털롤렛스킵을 끄거나 킵니다."
                    , "양털룰렛스킵"
            );
            cc.addCommand(
                    this.getClass().getMethod("helpeggs", CommandSender.class, Integer.class, Integer.class, String[].class)
                    , new ArgsTypes[]{ArgsTypes.INTEGER, ArgsTypes.INTEGER}, mc.getString("접두어") + "/알지급 <최소> <최대> - 지정된 값만큼 모든 유저에게 알을 지급하는 롤렛을 돌립니다."
                    , "알지급"
            );
            cc.addCommand(
                    this.getClass().getMethod("addDebuff", CommandSender.class, String[].class)
                    , new ArgsTypes[]{}, mc.getString("접두어") + "/디버프 - 디버프를 줍니다."
                    , "디버프"
            );
            cc.addCommand(
                    this.getClass().getMethod("sheepareasetting", CommandSender.class, String.class, String[].class)
                    , new ArgsTypes[]{ArgsTypes.STRING}, mc.getString("접두어") + "/양영역설정 <이름> - 양영역을 설정합니다."
                    , "양영역설정"
            );
            cc.addCommand(
                    this.getClass().getMethod("sheeparealist", CommandSender.class, String[].class)
                    , new ArgsTypes[]{}, mc.getString("접두어") + "/양영역정보 - 설정된 양영역의 정보를 표시합니다."
                    , "양영역정보"
            );
            cc.addCommand(
                    this.getClass().getMethod("sheepareadelete", CommandSender.class, String.class, String[].class)
                    , new ArgsTypes[]{ArgsTypes.STRING}, mc.getString("접두어") + "/양영역삭제 <이름> - 양영역을 삭제합니다."
                    , "양영역삭제"
            );
        }catch(Exception e) {
        }
        cc.load();
        //인벤토리
        pic.setPageInventoryClass(CustomInventory.class);
        pic.createInventory("납품", 54);
        pic.getInventory("납품").addpage();
        pic.load();
        Bukkit.getPluginManager().registerEvents(this, this);
        //업데이트
        Update();
        //보스바
        bar = Bukkit.createBossBar(mc.getString("BossbarName").replace("<COUNT>", price+"").replace("<MAX>", max+""), BarColor.WHITE, BarStyle.SOLID);
        bar.setVisible(true);
        bar1 = Bukkit.createBossBar(mc.getString("Bossbar1Name").replace("<COUNT>", "0"), BarColor.WHITE, BarStyle.SOLID);
        bar1.setVisible(true);
        bar1.setProgress(1);
        for(Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
            bar1.addPlayer(p);
        }
        //스코어보드 플러그인 로드
        BukkitRunnable brun = new BukkitRunnable() {
            @Override
            public void run() {
                if(Bukkit.getPluginManager().isPluginEnabled("PogoinScoreboard")) {
                    scoreboard = (PogoinScoreboard) Bukkit.getPluginManager().getPlugin("PogoinScoreboard");
                }
            }
        };brun.runTaskLater(this, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        save();
        bar.removeAll();
        bar1.removeAll();
    }

    @EventHandler
    public void save(WorldSaveEvent e) {
        save();
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        bar.addPlayer(e.getPlayer());
        bar1.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        bar.removePlayer(e.getPlayer());
        bar1.removePlayer(e.getPlayer());
        settermap.remove(e.getPlayer());
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if(isGameing) {
            e.setKeepInventory(true);
            Inventory inv = e.getEntity().getInventory();
            e.getDrops().clear();
            for(int i = 0; i<inv.getSize(); i++) {
                if(inv.getItem(i)!=null) {
                    Material m = inv.getItem(i).getType();
                    if(!m.equals(Material.WOODEN_SWORD)&&!m.equals(Material.STONE_SWORD)
                    &&!m.equals(Material.IRON_SWORD)&&!m.equals(Material.DIAMOND_SWORD)&&!m.equals(Material.NETHERITE_SWORD)
                    &&!m.equals(Material.SHEARS)) {
                        inv.setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
            deathsound = true;
        }
    }

    @EventHandler
    public void inter(PlayerInteractEvent e) {
        if(settermap.get(e.getPlayer())!=null&&e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            AreaSetter setter = settermap.get(e.getPlayer());
            if(setter.pos1!=null) {
                Vector v1 = setter.pos1;
                Vector v2 = e.getClickedBlock().getLocation().toVector().add(new Vector(0.5, 0.5, 0.5));
                e.getPlayer().sendMessage(mc.getString("접두어") + mc.getString("영역설정완료").replace("<POS>", mc.getString("pos2")).replace("<LOC>", mc.getString("loc").replace("<X>", ((int)v2.getX())+"").replace("<Y>", ((int)v2.getY())+"").replace("<Z>", ((int)v2.getZ())+"")));
                ShepherdArea area = new ShepherdArea(new Location(e.getClickedBlock().getWorld(), (v1.getX()+v2.getX())*0.5D, (v1.getY()+v2.getY())*0.5D, (v1.getZ()+v2.getZ())*0.5D), v1.clone().add(v2.clone().multiply(-1D)).multiply(0.5D));
                areamap.put(setter.string, area);
                settermap.remove(e.getPlayer());
                e.getPlayer().sendMessage(mc.getString("접두어") + mc.getString("영역생성완료").replace("<NAME>", setter.string));
            }else {
                setter.pos1=e.getClickedBlock().getLocation().toVector().add(new Vector(0.5, 0.5, 0.5));
                Vector v1 = setter.pos1;
                e.getPlayer().sendMessage(mc.getString("접두어") + mc.getString("영역설정완료").replace("<POS>", mc.getString("pos1")).replace("<LOC>", mc.getString("loc").replace("<X>", ((int)v1.getX())+"").replace("<Y>", ((int)v1.getY())+"").replace("<Z>", ((int)v1.getZ())+"")));
            }
            e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1.5f);
            e.setCancelled(true);
        }
    }
}
