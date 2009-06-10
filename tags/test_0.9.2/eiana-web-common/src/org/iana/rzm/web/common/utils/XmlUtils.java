package org.iana.rzm.web.common.utils;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;


public class XmlUtils {

    /**
     * Find the first ocurance of the element with the suplied name in the xml tree
     * @param content the xml trree to look in
     * @param name the element name to find
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Element findFirstElementByName(Element content, String name){

        if(content.getName().equals(name)){
            return content;
        }

        List<Element> list = content.getChildren();
        for (Element element : list) {
            if(element.getName().equals(name)){
                return element;
            }
        }

        for (Element element : list) {
            Element byName = findFirstElementByName(element, name);
            if(byName != null){
                return byName;
            }
        }

        return null;
    }

    /**
     * Find all the ocurance of the element with the suplied name in the xml tree
     * @param content the xml trree to look in
     * @param name the element name to find
     * @return list with all elements found or an empty list if none was found
     */
    @SuppressWarnings("unchecked")
    public static List<Element> findElementByName(Element content, String name) {

        List<Element>result = new ArrayList<Element>();

        if(content.getName().equals(name)){
            result.add(content);
        }

        List<Element> list = content.getChildren();
        for (Element element : list) {
            if(element.getName().equals(name)){
                result.add(element);
            }else{
                result.addAll(findElementByName(element, name));
            }

        }

        return result;
    }
}
