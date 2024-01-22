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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    public static ArrayList<Roulette> roulettes = new ArrayList<>();
    public BossBar bar;

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
                    }
                }
                double d = ((double)price)/((double)max);
                bar.setProgress(d<1?d:1);
                bar.setTitle(isGameing?mc.getString("BossbarName").replace("<COUNT>", price+"").replace("<MAX>", max+""):mc.getString("게임준비중"));
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
    }

    public static void addcount(int a) {
        if(max+a>maxtomax) {
            int i = a-(maxtomax-max);
            PSHEP.Title(PSHEP.mc.getString("양털감소타이틀1").replace("<NUM>", i+""), PSHEP.mc.getString("양털감소타이틀2").replace("<NUM>", i+""), 0, 20, 10);
            price-=i;
            max = maxtomax;
            if(price<0) {
                price=0;
            }
        }else {
            max+=a;
            PSHEP.Title(PSHEP.mc.getString("양털증가타이틀3").replace("<NUM>", a+""), PSHEP.mc.getString("양털증가타이틀4").replace("<NUM>", a+""), 0, 20, 10);
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
        }else {
            commandSender.sendMessage(mc.getString("접두어") + mc.getString("게임시작false"));
        }
    }

    public static void setskip(CommandSender commandSender, String... strings) {
        isskip=!isskip;
        commandSender.sendMessage(mc.getString("접두어") + mc.getString("롤렛스킵설정").replace("<VAULE>", isskip+""));
    }

    public static void helpeggs(CommandSender commandSender, Integer i1, Integer i2, String... strings) {
        if(isGameing) {
            roulettes.add(new HelpRoulette(r.nextInt(i2-i1)+i1));
        }else {
            commandSender.sendMessage(mc.getString("접두어") + mc.getString("게임시작false"));
        }
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
        }catch(Exception e) {
        }
        cc.load();
        //인벤토리
        pic.setPageInventoryClass(CustomInventory.class);
        pic.createInventory("납품", 54);
        pic.getInventory("납품").addpage();
        pic.load();
        Bukkit.getPluginManager().registerEvents(this, this);
        Update();
        bar = Bukkit.createBossBar(mc.getString("BossbarName").replace("<COUNT>", price+"").replace("<MAX>", max+""), BarColor.WHITE, BarStyle.SOLID);
        bar.setVisible(true);
        for(Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        save();
        bar.removeAll();
    }

    @EventHandler
    public void save(WorldSaveEvent e) {
        save();
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        bar.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        bar.removePlayer(e.getPlayer());
    }
}
