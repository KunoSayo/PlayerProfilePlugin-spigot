package yutang.yys;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import yutang.yys.Configure.CfgManager;
import yutang.yys.Configure.PlayerData;
import yutang.yys.Inventory.PlayerProfileInventoryClass;

import java.io.File;
import java.util.List;

import static yutang.yys.Configure.PlayerData.isDataInvalid;
import static yutang.yys.Inventory.PlayerProfileInventoryClass.getPlayerProfileGui;

/**
 * 我群鱼塘群号:617745343
 *
 * @author 阴阳师
 */
public class PlayerProfilePlugin extends JavaPlugin {
    private CfgManager config;
    public String showItemListPath = "item.showitems";
    public final static String[] armorspaths = {"item.helmet", "item.chest", "item.legs", "item.boots"};

    //不听不听 无视无视
    @SuppressWarnings("ConstantConditions")
    @Deprecated
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "profile": {
                if (args.length == 0) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("[Profile]该指令仅支持玩家自己输入。");
                        return true;
                    }
                    Player p = (Player) sender;
                    p.openInventory(getPlayerProfileGui(p));
                    return true;
                }
                //以上是纯/profile  下面看时搜索子命令
                switch (args[0].toLowerCase()) {
                    case "help": {
                        sender.sendMessage("------------------玩家资料帮助------------------");
                        sender.sendMessage("/profile [玩家]      打开自己或其他玩家的个人资料");
                        sender.sendMessage("/profile showitem    展示手上物品到个人资料上");
                        sender.sendMessage("/profile showitems   查看已展示的物品的序号");
                        sender.sendMessage("/profile settings    个人资料设置");
                        sender.sendMessage("/profile info        查看自己个人资料信息");
                        sender.sendMessage("/profile admin       管理指令");
                        return true;
                    }
                    case "showeditems":
                    case "shownitems":
                    case "showitems": {//查询展示的物品-------------------------------------------------------------------------------------------
                        Player p;
                        if (sender.hasPermission("playerprofile.op.queryshowitems") && args.length == 2) {
                            p = Bukkit.getPlayerExact(args[1]);
                            if (p == null) {
                                p = (Player) Bukkit.getOfflinePlayer(args[1]);
                            }
                        } else {
                            if (!(sender instanceof Player)) {
                                return false;
                            }
                            p = (Player) sender;
                        }
                        PlayerData pd = new PlayerData(this, p);
                        List<ItemStack> items = pd.getShowedItems();
                        int index = 0;
                        for (ItemStack item : items) {
                            sender.sendMessage(index + ":" + item.getType() + "，数量:" + item.getAmount());
                            index++;
                        }
                        return true;
                    }//showitems case
                    case "show":
                    case "showitem": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        Player p = (Player) sender;
                        if (p.getItemInHand().getType() == Material.AIR) {
                            p.sendMessage("[Profile]你想展示空气吗？？？？？？");
                            return true;
                        }
                        PlayerData pd = new PlayerData(this, p);
                        if (pd.cfg.getInt(PlayerData.pShowItemsMaxPath) != 0) {
                            int size;
                            size = pd.getShowingItemCount();
                            if (size >= pd.cfg.getInt(PlayerData.pShowItemsMaxPath)) {
                                sender.sendMessage("[Profile]你展示的物品超过限制了[已展示物品数:" + size + "你的限制" + pd.cfg.getInt(PlayerData.pShowItemsMaxPath) + "]");
                                return true;
                            }
                        } else {
                            sender.sendMessage("[Profile]你展示的物品超过限制了");
                        }
                        if (pd.addShowItem(p.getItemInHand()).save()) {
                            p.sendMessage("[Profile]新的展示物品添加成功");
                        } else {
                            p.sendMessage("[Profile]插件内部错误，请联系管理员查看后台。");
                        }
                        return true;
                    }//showitem case
                    case "removeitem":
                    case "removeitems":
                    case "removeshowitem": {
                        if (args.length == 1) {
                            return false;
                        }
                        int n = 2;
                        Player p = null;
                        OfflinePlayer op = null;
                        if (sender.hasPermission("playerprofile.op.removeshowitem")) {
                            //获得玩家orElse离线玩家
                            p = Bukkit.getPlayerExact(args[1]);
                            if (p == null) {
                                op = Bukkit.getOfflinePlayer(args[1]);
                            }
                        }
                        if (p == null && isDataInvalid(this, op.getName())) {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage("[Profile]未找到该玩家");
                                return true;
                            }
                            p = (Player) sender;
                            n = 1;
                        }
                        final Player player = p;
                        final OfflinePlayer offlinePlayer = op;
                        PlayerData pd = player != null ? new PlayerData(this, player) : new PlayerData(this, offlinePlayer);
                        if (pd.getShowingItemCount() == 0) {
                            sender.sendMessage("该玩家并没有展示物品");
                        }
                        int indexes[] = new int[args.length - n];
                        try {
                            for (int index = 0; n < args.length; n++, index++) {
                                int i = Integer.parseInt(args[n]);
                                if (i >= pd.getShowingItemCount() - 1)
                                    continue;
                                indexes[index] = i;
                            }
                        } catch (Exception e) {
                            return true;
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    if (pd.removeShowItem(indexes).save()) {
                                        if (player != null) {
                                            sender.sendMessage("[Profile]成功移除" + pd.getOwnerName() + "的指定展示物品");
                                        }
                                    }
                                } catch (Exception e) {
                                    if (player != null) {
                                        sender.sendMessage("[Profile]移除" + pd.getOwnerName() + "的展示物品失败");
                                    }
                                }
                            }
                        }.runTaskAsynchronously(this);
                        return true;
                    }//removeitem case
                    case "reload": {
                        if (!sender.hasPermission("profile.admin.reload")) {
                            sender.sendMessage("[Profile]你没有权限profile.admin.reload来重载插件");
                            config.reloadConfig();
                            return true;
                        }
                    }
                    case "settings": {
                        if (args.length == 1 || args.length == 2) {
                            sender.sendMessage("--------个人资料设置[部分功能需要服务器允许]--------");
                            sender.sendMessage("/profile settings showinventory <true/false>  在线时显示背包物品");
                            sender.sendMessage("/profile settings showitems <true/false> 展示物品");
                            sender.sendMessage("/profile settings showarmors <true/false> 显示装备");
                        }
                        switch (args[2].toLowerCase()) {
                            case "showinventory": {
                                switch (args[3].toLowerCase()) {
                                    case "t":
                                    case "true":

                                }
                            }
                        }
                    }
                    default: {
                        if (sender instanceof Player) {
                            if (Bukkit.getPlayer(args[0]) != null) {
                                Player p = Bukkit.getPlayer(args[0]);
                                PlayerData pd = new PlayerData(this, p);
                                if (pd.isDataInvalid()) {
                                    sender.sendMessage("[Profile]没有找到该玩家或这是一个错误的子命令，输入/profile help来查看所有指令");
                                    return true;
                                }
                                if (!pd.cfg.getBoolean("config.show-profile")) {
                                    sender.sendMessage("[Profile]" + p.getName() + "没有公开个人资料。");
                                    return true;
                                }
                                ((Player) sender).openInventory(getPlayerProfileGui(p));
                                return true;
                            } else {
                                if (Bukkit.getOfflinePlayer(args[0]) != null) {
                                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                                    PlayerData pd = new PlayerData(this, p);
                                    if (pd.isDataInvalid()) {
                                        sender.sendMessage("[Profile]没有找到该玩家或这是一个错误的子命令，输入/profile help来查看所有指令");
                                        return true;
                                    }
                                    if (!pd.cfg.getBoolean("config.show-profile")) {
                                        sender.sendMessage("[Profile]" + p.getName() + "没有公开个人资料。");
                                        return true;
                                    }
                                    ((Player) sender).openInventory(getPlayerProfileGui(p));
                                    return true;
                                }
                            }
                        }//玩家才能打开玩家个人资料 不听不听qwq
                        sender.sendMessage("[Profile]这是一个错误的子命令，输入/profile help来查看所有指令");
                        return true;
                    }//第一个参数default
                }//第一个参数switch
            }//指令profile case
        }//指令switch
        return false;
    }

    @Override//开启--------------------------------------------------------------------
    public void onEnable() {
        PlayerProfileListener.plugin = this;
        new PlayerProfileInventoryClass(this);
        this.config = new CfgManager(this);
        this.config.initConfig();
        getServer().getPluginManager().registerEvents(new PlayerProfileListener(), this);
    }

    @Override
    public void onLoad() {
        File file = new File(getDataFolder() + "\\PlayerDatas");
        if (!file.exists()) {
            if (!file.mkdir()) {
                this.getLogger().info("[Profile]玩家数据文件夹创建失败");
            }
        }
    }
}
