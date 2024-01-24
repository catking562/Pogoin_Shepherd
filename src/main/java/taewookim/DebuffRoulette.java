package taewookim;

public class DebuffRoulette extends Roulette {

    int tick = 0;

    public DebuffRoulette(int i) {
        super(i);
    }

    @Override
    public void Update() {
        tick++;
        if(tick==1) {
            PSHEP.scoreboard.addDebuff(PSHEP.roulettes.size());
        }
        if(tick>90) {
            isEnd = true;
        }
    }
}
