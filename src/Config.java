import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class Config
 * Le patron de conception Singleton est implémenté
 * config permet d'analyser un fichier de configuration ecrit en XML
 * Les propriétés sont enregistrés dans un dictionnaire {NAME : VALUE}
 */
public class Config {
	
	private File file;                   		// le fichier de configuration 
	private Map<String, Object> fields;   		// le dictioannire des propriétés
	private static Config instance = null;      // l'instance partagée (Patron Singleton)
	
	/**
	 * Constructeur privé pour implémenter le patron de conception Singleton
	 * @param filePath le chemin relatif du fichier de configuration
	 * @throws XML error 
	 */
	private Config(String filePath) throws Exception {
		this.file = new File(filePath);
		this.fields = new HashMap<>();
		this.setFields();
	}
	
	/**
	 * @return instance de Config
	 */
	static public Config getInstance() {
		return instance;
	}
	
	/**
	 * Instanciation de Config
	 * @param filePath le chemin relatif du fichier de configuration
	 * @return instance de Config
	 * @throws Exception XML error
	 */
	public static Config getInstance(String filePath) throws Exception {
		instance = new Config(filePath);
		return instance;
	}
	
	public void setFields() throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
	    final Document document= builder.parse(this.file);
	    final Element root = document.getDocumentElement();
	    
        final NodeList nodes = root.getChildNodes();
        final int nb = nodes.getLength();
        for(int i=0; i<nb; i++) {
        	Node n = nodes.item(i);
        	if(n.getNodeType() == Node.ELEMENT_NODE) {
        		Element e = (Element)n;
        		if(e.getTagName().equals("file") == true) {
        			setFileList(e);
        		}
        		else {
            		setElement(e);	
        		}
        	}
        }
        
	}
	
	private void setFileList(Element file) {
		final NodeList nodes = file.getChildNodes();
		final int nb = nodes.getLength();

		for(int i=0; i<nb; i++) {
			Node n = nodes.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				setFile((Element) n);
			}
		}
	}
	
	private void setFile(Element item) {
		Map<String, String> fileConfig = new HashMap<>();
		final NodeList nodes = item.getChildNodes();
		final int nb = nodes.getLength();
		String key = null;
		
		for(int i=0; i<nb; i++) {
			Node n = nodes.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				String name = n.getNodeName();
				if(name.equals(Constant.Config.KEY) == true) {
					key = n.getTextContent();
				}
				else {
					fileConfig.put(n.getNodeName(), n.getTextContent());
				}
			}
		}
		
		this.fields.put(key, fileConfig);
	}
	
	private void setElement(Element e) {
		final NodeList nodes = e.getChildNodes();
		int nb = nodes.getLength();
		
		if(nb == 1) {
			String key = e.getTagName();
			String value = e.getTextContent();
			this.fields.put(key, value);
		}
		else {
			for(int i=0; i<nb; i++) {
				Node n = nodes.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					setElement((Element) n);
				}
			}
		}
	}
	
	/**
	 * lire une propriété
	 * @param key le nom de la propriété
	 * @return null si la propriété n'existe pas
	 */
	public Object getField(String key) {
		return this.fields.get(key);
	}
	
	/**
	 * Vérifie si un champ existe ou non
	 * @param key identifiant du champ
	 * @return true si key existe
	 */
	public Boolean getKey(String key) {
		return this.fields.containsKey(key);
	}
	
	/**
	 * lire une propriété d'un fichier
	 * @param fileKey l'identifiant du fichier
	 * @param key l'identifiant de la prorpiété
	 * @return la valeur de la proprièté (String)
	 */
	public String getField(String fileKey, String key) {
		Map<String, String> fileProp = (Map<String, String>) this.getField(fileKey);
		return fileProp.get(key);
	}
}
