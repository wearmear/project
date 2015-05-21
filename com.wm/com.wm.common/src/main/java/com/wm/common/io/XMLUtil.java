package com.wm.common.io;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSON;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.wm.common.exception.ExceptionConstant;
import com.wm.common.lang.constant.CharsetConstant;

public class XMLUtil {
	private static final Logger LOGGER = Logger.getLogger(XMLUtil.class);
	private static final String CHARSET_DEFAULT = CharsetConstant.UTF_8;

	public static void print(Node node) {
		print(node, CHARSET_DEFAULT);
	}

	/**
	 * 将node的XML字符串输出到控制台
	 * 
	 * @param node
	 * @param charset
	 */
	public static void print(Node node, String charset) {
		LOGGER.info("------------XML输出start,nodeName:" + node.getNodeName());
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, charset);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(node);
			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		LOGGER.info("------------XML输出end");
	}

	public static Node selectSingleNode(String express, Object source) {// 查找节点，并返回第一个符合条件节点
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath
					.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static NodeList selectNodes(String express, Object source) {// 查找节点，返回符合条件的节点集。
		NodeList result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (NodeList) xpath.evaluate(express, source,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void saveXml(String fileName, Document doc) {// 将Document输出到文件
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			DOMSource source = new DOMSource();
			source.setNode(doc);
			StreamResult result = new StreamResult();
			result.setOutputStream(new FileOutputStream(fileName));

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Document parse(String xmlStr)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LOGGER.warn("初始化DocumentBuilder失败", e);
			throw e;
		}
		Document doc = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xmlStr.getBytes());
			doc = db.parse(bais);
		} catch (SAXException e) {
			LOGGER.warn("请求参数转xml失败", e);
			throw e;
		} catch (IOException e) {
			LOGGER.warn(ExceptionConstant.IO, e);
			throw e;
		}
		return doc;
	}

	public static byte[] parse(Document xmldoc) {
		return parse(xmldoc, CHARSET_DEFAULT);
	}

	public static byte[] parse(Document xmldoc, String charset) {
		// 转成String：
		StringWriter strResponse = null;
		OutputFormat format = new OutputFormat(xmldoc);
		format.setEncoding(charset);
		format.setStandalone(true);
		format.setIndenting(true);
		format.setIndent(2);
		format.setLineSeparator(LineSeparator.Windows);
		strResponse = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(strResponse, format);
		try {
			serializer.asDOMSerializer();
			serializer.serialize(xmldoc);
		} catch (IOException ex1) {
		}
		StringBuffer sb = strResponse.getBuffer();

		// // 再由String转成InputStream
		// InputStream inputStream = new ByteArrayInputStream(sb.toString()
		// .getBytes());
		return sb.toString().getBytes();
	}

	/**
	 * json转xml
	 * @param json
	 * @param rootName
	 * @return
	 */
	public static String parseJSON2XML(JSON json, String rootName, String charset){
		LOGGER.debug("json:"+json);
		String xmlStr = null;
		net.sf.json.xml.XMLSerializer xmlSerializer = new net.sf.json.xml.XMLSerializer();
		xmlSerializer.setTypeHintsEnabled(false);
		xmlSerializer.setRootName(rootName);
		xmlStr = xmlSerializer.write(json, charset);
		LOGGER.debug("xmlStr:"+xmlStr);
		return xmlStr;
	}

	/**
	 * xml转json
	 * @param xmlStr
	 * @return
	 */
	public static JSON parseXML2JSON(String xmlStr){
		LOGGER.debug("xmlStr:"+xmlStr);
		JSON json = null;
		net.sf.json.xml.XMLSerializer xmlSerializer = new net.sf.json.xml.XMLSerializer();
		json = xmlSerializer.read(xmlStr);
		LOGGER.debug("json:"+json);
		return json;
	}
	
	public static void main(String[] args) {
//		String jsonStr = "{\"HEAD\":{\"UUID\":\"54CC9D10-C442-4288-A3F9-61F782B6AA44\",\"RESCODE\":\"1\",\"ERRORMSG\":\"成功\"}," +
//				"\"BODY\":[[[{\"REGISTNO\":\"RDAA201332010000000367\",\"DAMAGEADDRESS\":\"13641051703\",\"LICENSENO\":\"苏K56454\",\"LINKERNAME\":\"报案人\",\"LINKERMOBILE\":\"13641051703\",\"DAMAGEDATE\":\"2013-03-26\",\"DAMAGEHOUR\":\"16:53:26\",\"REPORTDATE\":\"2013-04-25\",\"REPORTHOUR\":\"16:53:26\",\"LONGITUDE\":\"116.26886925758\",\"LATITUDE\":\"39.891207744424\",\"BRANDCODE\":\"320000172\",\"SENDFLAG\":\"是\",\"SURVEYFLAG\":\"否\",\"AGENTCODE\":\"3201009000032\",\"SURVEYORCODE\":[],\"SURVEYORNAME\":[],\"CUSTOMLEVEL\":\"/推荐修理\",\"COMPLETEFLAG\":\"否\",\"REMARK\":\"驾驶人于2013-03-26\t16:53:26在13641051703使用被保险机动车过程中，发生碰撞。导致主车损失、车辆损失不详，人员伤亡情况不详，目前损失标的位于13641051703，需进行查勘。\"},{\"REGISTNO\":\"RDAA201332010000000365\",\"DAMAGEADDRESS\":\"出险地点\",\"LICENSENO\":\"苏K56454\",\"LINKERNAME\":\"报案人\",\"LINKERMOBILE\":\"13641051703\",\"DAMAGEDATE\":\"2013-03-26\",\"DAMAGEHOUR\":\"16:47:34\",\"REPORTDATE\":\"2013-04-25\",\"REPORTHOUR\":\"16:47:34\",\"LONGITUDE\":\"116.405285\",\"LATITUDE\":\"39.904989\",\"BRANDCODE\":\"320000172\",\"SENDFLAG\":\"否\",\"SURVEYFLAG\":\"否\",\"AGENTCODE\":[],\"SURVEYORCODE\":[],\"SURVEYORNAME\":[],\"CUSTOMLEVEL\":\"/推荐修理\",\"COMPLETEFLAG\":\"否\",\"REMARK\":\"驾驶人于2013-03-26\t16:47:34在出险地点使用被保险机动车过程中，发生碰撞。导致主车损失、车辆损失不详，人员伤亡情况不详，目前损失标的位于出险地点，需进行查勘。\"},{\"REGISTNO\":\"RDAA201332010000000668\",\"DAMAGEADDRESS\":\"北京市昌平区沙河\",\"LICENSENO\":\"苏K56454\",\"LINKERNAME\":\"asd\",\"LINKERMOBILE\":\"12322222222\",\"DAMAGEDATE\":\"2013-04-25\",\"DAMAGEHOUR\":\"15:13:19\",\"REPORTDATE\":\"2013-04-25\",\"REPORTHOUR\":\"15:13:19\",\"LONGITUDE\":[],\"LATITUDE\":[],\"BRANDCODE\":\"320000172\",\"SENDFLAG\":\"否\",\"SURVEYFLAG\":\"否\",\"AGENTCODE\":[],\"SURVEYORCODE\":[],\"SURVEYORNAME\":[],\"CUSTOMLEVEL\":\"/推荐修理\",\"COMPLETEFLAG\":\"否\",\"REMARK\":\"驾驶人于2013-04-25\t15:13:19在北京市昌平区沙河使用被保险机动车过程中，发生碰撞。导致主车损失、车辆损失不详，人员伤亡情况不详，目前损失标的位于北京市昌平区沙河，需进行查勘。\"},{\"REGISTNO\":\"RDAA201332010000000667\",\"DAMAGEADDRESS\":\"知春路\",\"LICENSENO\":\"苏K56454\",\"LINKERNAME\":\"dsfs\",\"LINKERMOBILE\":\"12322222222\",\"DAMAGEDATE\":\"2013-04-25\",\"DAMAGEHOUR\":\"14:46:48\",\"REPORTDATE\":\"2013-04-25\",\"REPORTHOUR\":\"14:46:48\",\"LONGITUDE\":[],\"LATITUDE\":[],\"BRANDCODE\":\"320000172\",\"SENDFLAG\":\"否\",\"SURVEYFLAG\":\"否\",\"AGENTCODE\":[],\"SURVEYORCODE\":[],\"SURVEYORNAME\":[],\"CUSTOMLEVEL\":\"/推荐修理\",\"COMPLETEFLAG\":\"否\",\"REMARK\":\"驾驶人于2013-04-25\t14:46:48在知春路使用被保险机动车过程中，发生碰撞。导致主车损失、车辆损失不详，人员伤亡情况不详，目前损失标的位于知春路，需进行查勘。\"},{\"REGISTNO\":\"RDAA201332010000000665\",\"DAMAGEADDRESS\":\"知春路\",\"LICENSENO\":\"苏K56454\",\"LINKERNAME\":\"asd\",\"LINKERMOBILE\":\"12323233333\",\"DAMAGEDATE\":\"2013-04-25\",\"DAMAGEHOUR\":\"14:35:29\",\"REPORTDATE\":\"2013-04-25\",\"REPORTHOUR\":\"14:35:29\",\"LONGITUDE\":[],\"LATITUDE\":[],\"BRANDCODE\":\"320000172\",\"SENDFLAG\":\"否\",\"SURVEYFLAG\":\"否\",\"AGENTCODE\":[],\"SURVEYORCODE\":[],\"SURVEYORNAME\":[],\"CUSTOMLEVEL\":\"/推荐修理\",\"COMPLETEFLAG\":\"否\",\"REMARK\":\"驾驶人于2013-04-25\t14:35:29在知春路使用被保险机动车过程中，发生碰撞。导致主车损失、车辆损失不详，人员伤亡情况不详，目前损失标的位于知春路，需进行查勘。\"},{\"REGISTNO\":\"RDAA201332010000000377\",\"DAMAGEADDRESS\":\"12\",\"LICENSENO\":\"苏K56454\",\"LINKERNAME\":\"黄晓明\",\"LINKERMOBILE\":\"11111111111\",\"DAMAGEDATE\":\"2013-03-27\",\"DAMAGEHOUR\":\"10:29:46\",\"REPORTDATE\":\"2013-04-25\",\"REPORTHOUR\":\"10:29:46\",\"LONGITUDE\":\"116.48753416966\",\"LATITUDE\":\"39.881997673397\",\"BRANDCODE\":\"320000172\",\"SENDFLAG\":\"否\",\"SURVEYFLAG\":\"否\",\"AGENTCODE\":[],\"SURVEYORCODE\":[],\"SURVEYORNAME\":[],\"CUSTOMLEVEL\":\"/推荐修理\",\"COMPLETEFLAG\":\"否\",\"REMARK\":\"驾驶人于2013-03-27\t10:29:46在12使用被保险机动车过程中，发生碰撞。导致主车损失、车辆损失不详，人员伤亡情况不详，目前损失标的位于12，需进行查勘。\"}]]]}";
//		JSON json = JSONObject.fromObject(jsonStr);
//		String xmlStr = parseJSON2XML(json, "packet", "GBK");
		
		String xmlStr = "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"yes\"?>"
+"<PACKET>"
+"  <BODY>"
+"    <PRPLFLCLAIMINFOINFO>"
+"      <PRPLFLCLAIMINFOLIST>"
+"        <PRPLFLCLAIMINFO>"
+"          <REGISTNO>RDAA201332010000000367</REGISTNO>"
+"          <DAMAGEADDRESS>13641051703</DAMAGEADDRESS>"
+"          <LICENSENO>苏K56454</LICENSENO>"
+"          <LINKERNAME>报案人</LINKERNAME>"
+"        </PRPLFLCLAIMINFO>"
+"      </PRPLFLCLAIMINFOLIST>"
+"    </PRPLFLCLAIMINFOINFO>"
+"  </BODY>"
+"</PACKET>"
+"<!-- 查勘员负责的案件列表 -->";
		
		parseXML2JSON(xmlStr);
	}

}