package com.simon.xml;

import com.simon.game.configuration.GameConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class XmlPersister {
	private String path;
	private static XmlPersister theInstance;
	public static XmlPersister instance(){
		if (theInstance==null) theInstance=new XmlPersister("src/main/resources/game/config/gameConfig.xml");
		return theInstance;
	}
	public XmlPersister(String p) {
		path=p;
	}
	public GameConfig load() throws Exception{
		JAXBContext jaxbContext = JAXBContext.newInstance(GameConfig.class);
		FileInputStream is = new FileInputStream(path);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		GameConfig result = (GameConfig) unmarshaller.unmarshal(is);
		is.close();
		return result;
	}
	public void store(GameConfig gameConfig)throws Exception{
		JAXBContext jaxbContext = JAXBContext.newInstance(GameConfig.class);
		FileOutputStream os = new FileOutputStream(path);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.marshal(gameConfig, os);
		os.flush();
		os.close();
	}
}
