package org.andy.fx.gui.main.table_panels;

public class TabMask {
	
	private TabMask(){}
	
	public enum Tab {
		TIME(0), TRAVEL(1), OFFER(2), INVOICE(3), ORDER(4), DELIVERY(5), PURCHASE(6),
	    EXPENSES(7), TAX(8), RESULT(9), SETTINGS(10), MIGRATION(11), TRIALS(12);

	    public final int bitIndex;
	    public final int bit;
	    Tab(int i){ this.bitIndex = i; this.bit = 1 << i; }
	}

    public static int show(int mask, Tab t)      { return mask |  t.bit; }
    public static int hide(int mask, Tab t)      { return mask & ~t.bit; }
    public static boolean visible(int mask, Tab t){ return (mask & t.bit) != 0; }

    // Aus Liste bauen
    public static int of(Tab... tabs){
        int m = 0; for (Tab t: tabs) m |= t.bit; return m;
    }
    
}
