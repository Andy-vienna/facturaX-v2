package org.andy.fx.gui.main.table_panels;

public class TabMask {
	
	private TabMask(){}
	
	public enum Tab {
		OFFER(0), INVOICE(1), ORDER(2), DELIVERY(3), PURCHASE(4),
	    EXPENSES(5), TAX(6), RESULT(7), SETTINGS(8), MIGRATION(9), TRIALS(10);

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
