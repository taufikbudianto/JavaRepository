package com.taufik.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.taufik.util.Response;
import com.taufik.util.ResponseData;
import com.taufik.util.ResponseData.DataGet;
import com.taufik.util.Util;

import lombok.Data;

@RestController
public class FormulatrixRepoController {

	SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
	String strDate = formatter.format(new Date());
	File file;
	LocalDateTime now = LocalDateTime.now();
	int year = now.getYear();
	int month = now.getMonthValue();
	String monthVal = Util.getMonthForInt(month);

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody DataPost data) {

		Response resp = new Response();
		try {
			String f = "C:\\Repository\\" + year + "\\" + monthVal;
			if (data.getItemType() == 1) {

				file = new File(f);
				JSONObject obj;
				if (!file.exists()) {
					if (file.mkdirs()) {

					}

				}
				String f2 = f + "\\" + data.getItemName() + ".json";
				File file2 = new File(f2);
				obj = new JSONObject();
				obj.put("content", data.getItemContent());
				FileWriter fileW = new FileWriter(f + "\\" + data.getItemName() + "_" + strDate + ".json");
				fileW.write(obj.toJSONString());
				fileW.flush();

			} else if (data.getItemType() == 2) {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("DataXML");
				doc.appendChild(rootElement);
				Element nama = doc.createElement(data.getItemName());
				rootElement.appendChild(nama);
				Element content = doc.createElement("content");
				content.appendChild(doc.createTextNode(data.getItemContent()));
				nama.appendChild(content);

				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(
						new File(f + "\\" + data.getItemName() + "_" + strDate + ".xml"));

				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				System.out.println("File saved!");
			} else {
				resp.setResponse("XX");
				resp.setResponseValue("Gagal Harap Masukan type data");
				return new ResponseEntity<Response>(resp, HttpStatus.BAD_REQUEST);
			}
			resp.setResponse("00");
			resp.setResponseValue("Save Data Sukses");
			return new ResponseEntity<Response>(resp, HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			resp.setResponse("XX");
			resp.setResponseValue("Gagal Save Data");
			System.out.println(e.getMessage());
			return new ResponseEntity<Response>(resp, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/retrieve/{itemname}", method = RequestMethod.GET)
	public ResponseEntity<?> retieve(@PathVariable String itemname) {
		JSONParser parser = new JSONParser();
		String f = "C:\\Repository";
		File file = new File(f);
		File[] listOfFiles = file.listFiles();
		List<DataGet> list = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (Util.getFileExtension(listOfFiles[i]).equals("json")) {

				} else if (Util.getFileExtension(listOfFiles[i]).equals("xml")) {

				}
			} else if (listOfFiles[i].isDirectory()) {
				File[] listOfFile2 = listOfFiles[i].listFiles();
				for (int j = 0; j < listOfFile2.length; j++) {
					if (listOfFile2[j].isFile()) {
						if (Util.getFileExtension(listOfFile2[j]).equals("json")) {

						} else if (Util.getFileExtension(listOfFile2[j]).equals("xml")) {

						}
					} else if (listOfFile2[j].isDirectory()) {
						File[] listOfFile3 = listOfFile2[j].listFiles();
						for (int k = 0; k < listOfFile3.length; k++) {
							DataGet dataget = new DataGet();
							Integer no = 1;
							if (listOfFile3[k].isFile()) {
								if (listOfFile3[k].getName().substring(0, listOfFile3[k].getName().length() - 20)
										.equals(itemname)) {
									if (Util.getFileExtension(listOfFile3[k]).equals("json")) {
										// System.out.println(listOfFile3[k].getName().substring(0,
										// listOfFile3[k].getName().length()-20));
										Object obj = null;

										try {
											obj = parser.parse(new FileReader(listOfFile3[k].getPath()));
											JSONObject jsonObject = (JSONObject) obj;
											System.out.println(jsonObject);
											String content = (String) jsonObject.get("content");
											dataget.setItemContent(content);
											dataget.setNo(1);
											no++;
											list.add(dataget);
										} catch (IOException | ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}

								}

								if (listOfFile3[k].getName().substring(0, listOfFile3[k].getName().length() - 19)
										.equals(itemname)) {
									if (Util.getFileExtension(listOfFile3[k]).equals("xml")) {
										File fXmlFile = new File(listOfFile3[k].getPath());
										DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
										DocumentBuilder dBuilder;

										try {
											dBuilder = dbFactory.newDocumentBuilder();
											Document doc = dBuilder.parse(fXmlFile);
											doc.getDocumentElement().normalize();
											NodeList nList = doc.getElementsByTagName(itemname);
											for (int temp = 0; temp < nList.getLength(); temp++) {

												Node nNode = nList.item(temp);
												if (nNode.getNodeType() == Node.ELEMENT_NODE) {
													Element eElement = (Element) nNode;
													dataget.setNo(2);
													dataget.setItemContent(eElement.getElementsByTagName("content").item(0)
															.getTextContent());
													list.add(dataget);
												}

											}
										} catch (ParserConfigurationException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (SAXException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								}
							} else if (listOfFile3[k].isDirectory()) {

							}
						}
					}
				}
			}
		}
		ResponseData resp = new ResponseData();
		resp.setResponse("00");
		resp.setResponseValue("OK");
		resp.setData(list);
		return new ResponseEntity<ResponseData>(resp, HttpStatus.OK);
	}

	@RequestMapping(value = "/gettype/{itemname}", method = RequestMethod.GET)
	public List<Integer> getType(@PathVariable String itemname) {
		String f = "C:\\Repository";
		File file = new File(f);
		File[] listOfFiles = file.listFiles();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				File[] listOfFile2 = listOfFiles[i].listFiles();
				for (int j = 0; j < listOfFile2.length; j++) {
					if (listOfFile2[j].isFile()) {

					} else if (listOfFile2[j].isDirectory()) {
						File[] listOfFile3 = listOfFile2[j].listFiles();
						for (int k = 0; k < listOfFile3.length; k++) {
							if (listOfFile3[k].isFile()) {
								if (listOfFile3[k].getName().substring(0, listOfFile3[k].getName().length() - 20)
										.equals(itemname)) {
									list.add(1);
								}if (listOfFile3[k].getName().substring(0, listOfFile3[k].getName().length() - 19)
										.equals(itemname)) {
									list.add(2);
									
								}
							} else if (listOfFile3[k].isDirectory()) {

							}
						}
					}
				}
			}
		}
		return list;
	}
	@RequestMapping(value = "/deregister/{itemname}", method = RequestMethod.GET)
	public String delete(@PathVariable String itemname) {
		try {
			String f = "C:\\Repository";
			File file = new File(f);
			File[] listOfFiles = file.listFiles();
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					System.out.println("File " + listOfFiles[i].getName());
				} else if (listOfFiles[i].isDirectory()) {
					File[] listOfFile2 = listOfFiles[i].listFiles();
					for (int j = 0; j < listOfFile2.length; j++) {
						if (listOfFile2[j].isFile()) {

						} else if (listOfFile2[j].isDirectory()) {
							File[] listOfFile3 = listOfFile2[j].listFiles();
							for (int k = 0; k < listOfFile3.length; k++) {
								if (listOfFile3[k].isFile()) {
									if (listOfFile3[k].getName().substring(0, listOfFile3[k].getName().length() - 20)
											.equals(itemname)) {
										if(listOfFile3[k].delete()) {
											System.out.println("sukses delete");
										}
									}if (listOfFile3[k].getName().substring(0, listOfFile3[k].getName().length() - 19)
											.equals(itemname)) {
										if(listOfFile3[k].delete()) {
											System.out.println("sukses delete");
										}
									}
								} else if (listOfFile3[k].isDirectory()) {

								}
							}
						}
					}
				}
			}
			return "sukses";
		} catch (Exception e) {
			// TODO: handle exception
			return "gagal";
		}
		
	}

	@Data
	public static class DataPost {
		private String itemName;
		private String itemContent;
		private Integer itemType;
	}
}
