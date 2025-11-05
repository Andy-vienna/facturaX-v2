package org.andy.fx.code.eRechnung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Owner;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.mustangproject.Invoice;
import org.mustangproject.ZUGFeRD.Profiles;
import org.mustangproject.ZUGFeRD.ZUGFeRD2PullProvider;

public class XRechnungXML {

	public static void generateXRechnungXML(Rechnung rechnung, Bank bank, Kunde kunde, Owner owner, String sXmlName) throws ParseException, IOException {
		BufferedWriter writer = null;

		Invoice i = RechnungsDaten.doInvoice(rechnung, bank, kunde, owner);

		ZUGFeRD2PullProvider zf2p = new ZUGFeRD2PullProvider();
		zf2p.setProfile(Profiles.getByName("XRechnung"));
		zf2p.generateXML(i);
		String theXML = new String(zf2p.getXML());

		writer = new BufferedWriter(new FileWriter(sXmlName));
		writer.write(theXML);
		writer.close();
	}
}
