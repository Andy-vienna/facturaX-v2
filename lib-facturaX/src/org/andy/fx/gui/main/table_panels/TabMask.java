package org.andy.fx.gui.main.table_panels;

public class TabMask {
	
	private TabMask(){}
	
	public enum Tab {
		TRAVEL(0), OFFER(1), INVOICE(2), ORDER(3), DELIVERY(4), PURCHASE(5),
	    EXPENSES(6), TAX(7), RESULT(8), SETTINGS(9), MIGRATION(10), TRIALS(11);

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
