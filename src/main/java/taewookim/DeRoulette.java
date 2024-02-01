package taewookim;

import org.bukkit.Sound;

public class DeRoulette extends Roulette {
    public DeRoulette(int i) {
        super(i);
    }

    public void Update() {
        if(PSHEP.isskip) {
            PSHEP.dewool(i);
            isEnd = true;
            PSHEP.Title(PSHEP.mc.getString("양털증가타이틀3").replace("<NUM>", i+""), PSHEP.mc.getString("양털증가이틀4").replace("<NUM>", i+""), 0, 20, 10);
        }
        switch(a) {
            case 0:
                b++;
                if(PSHEP.roulette_start<b) {
                    b=0;
                    a++;
                }
                String string = "";
                for(int c = 0; c<size; c++) {
                    string+=PSHEP.r.nextInt(10);
                }
                PSHEP.Title(PSHEP.mc.getString("양털증가타이틀1").replace("<NUM>", string), PSHEP.mc.getString("양털증가타이틀2").replace("<NUM>", string), 0, 20, 0);
                PSHEP.Sound(Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                break;
            case 1:
                b++;
                if(PSHEP.roulette_ing+1<b) {
                    b=0;
                    a++;
                }
                String string1 = "";
                for(int c = 0; c<size; c++) {
                    if(1-(((double)b)/((double)PSHEP.roulette_ing))<((double)(c))/((double)size)) {
                        string1+=arrNum[c]+"";
                    }else {
                        string1+=PSHEP.r.nextInt(10);
                    }
                    if(1-(((double)b)/((double)PSHEP.roulette_ing))==((double)(c))/((double)size)) {
                        PSHEP.Sound(Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                    }
                }
                PSHEP.Title(PSHEP.mc.getString("양털증가타이틀1").replace("<NUM>", string1), PSHEP.mc.getString("양털증가타이틀2").replace("<NUM>", string1), 0, 20, 0);
                break;
            case 2:
                b++;
                if(b==1) {
                    PSHEP.Sound(Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    PSHEP.dewool(i);
                }
                if(PSHEP.roulette_final<b) {
                    isEnd = true;
                }
                break;
        }
    }
}
