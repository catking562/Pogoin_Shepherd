package taewookim;

import org.CatAndDomi.components.pageinventory.InventoryOpener;
import org.CatAndDomi.components.pageinventory.PageInventory;
import org.CatAndDomi.components.pageinventory.PageInventoryComponent;
import org.CatAndDomi.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomInventory extends PageInventory {

    public CustomInventory(String name, PageInventoryComponent component, Integer invsize) {
        super(name, component, invsize);
    }

    public CustomInventory(PageInventoryComponent component, YamlConfiguration config) {
        super(component, config);
    }

    @Override
    public void setinv_creating(Inventory inv) {
        super.setinv_creating(inv);
        ItemStack i = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(" ");
        i.setItemMeta(m);
        inv.setItem(45, i);
        inv.setItem(46, i);
        //inv.setItem(47, i);
        inv.setItem(48, i);
        inv.setItem(49, i);
        inv.setItem(50, i);
        //inv.setItem(51, i);
        inv.setItem(52, i);
        inv.setItem(53, i);
        i = new ItemStack(Material.valueOf(PSHEP.mc.getString("납품확인.아이템")));
        m = i.getItemMeta();
        m.setDisplayName(PSHEP.mc.getString("납품확인.이름"));
        m.setLore(PSHEP.mc.getList("납품확인.설명"));
        i.setItemMeta(m);
        inv.setItem(51, i);
        i = new ItemStack(Material.valueOf(PSHEP.mc.getString("납품취소.아이템")));
        m = i.getItemMeta();
        m.setDisplayName(PSHEP.mc.getString("납품취소.이름"));
        m.setLore(PSHEP.mc.getList("납품취소.설명"));
        i.setItemMeta(m);
        inv.setItem(47, i);
    }

    @Override
    public void click(InventoryClickEvent e, InventoryOpener opener) {
        super.click(e, opener);
        if(e.getClickedInventory().getHolder()==null&&e.getSlot()>44) {
            e.setCancelled(true);
            Inventory inv = e.getClickedInventory();
            switch(e.getSlot()) {
                case 47:
                    ((Player)e.getWhoClicked()).closeInventory();
                    for(int a = 0; a<45; a++) {
                        ItemStack i = inv.getItem(a);
                        if(i!=null&&!i.getType().equals(Material.AIR)) {
                            if(InventoryUtils.hasEnoughSpace(e.getWhoClicked().getInventory().getStorageContents(), i)) {
                                e.getWhoClicked().getInventory().addItem(i);
                            }else {
                                e.getWhoClicked().getWorld().dropItem(e.getWhoClicked().getLocation(), i);
                            }
                        }
                    }
                    break;
                case 51:
                    ((Player)e.getWhoClicked()).closeInventory();
                    int cc = 0;
                    for(int a = 0; a<45; a++) {
                        ItemStack i = inv.getItem(a);
                        if(i!=null&&!i.getType().equals(Material.AIR)) {
                            if(PSHEP.sellprice.containsKey(i.getType())&&PSHEP.sellprice.get(i.getType())>0) {
                                cc+=i.getAmount()*PSHEP.sellprice.get(i.getType());
                            }else {
                                if(InventoryUtils.hasEnoughSpace(e.getWhoClicked().getInventory().getStorageContents(), i)) {
                                    e.getWhoClicked().getInventory().addItem(i);
                                }else {
                                    e.getWhoClicked().getWorld().dropItem(e.getWhoClicked().getLocation(), i);
                                }
                            }
                        }
                    }
                    PSHEP.price+=cc;
                    if(cc>0) {
                        Bukkit.broadcastMessage(PSHEP.mc.getString("접두어") + PSHEP.mc.getString("납품완료").replace("<COUNT>", cc+""));
                    }
                    break;
            }
        }
    }
}
