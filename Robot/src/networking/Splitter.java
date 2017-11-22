package networking;

import java.util.ArrayList;

/**
 * Splits up passed messages to parse into objects
 */
public class Splitter {

	public static Object[] split(String data) {

		ArrayList<Object> list = new ArrayList<Object>();
		int saveIndex = 0;

		for (int i = 0; i < data.length(); i++){
			char c = data.charAt(i); 
			if(c == ','){
				String item = data.substring(saveIndex, i);
				list.add(item);
				saveIndex = i+1;
			}
			if(i == data.length() - 1){
				String item = data.substring(saveIndex);
				list.add(item);
			}
		}

		for(int i = 0; i < list.size(); i++){
			Object currItem = list.get(i);
			boolean changed = false;
			try{
				int item = Integer.parseInt((String) currItem);
				list.set(i, item);
				changed = true;
			}catch(NumberFormatException e){
				//e.printStackTrace();
			}
			if(!changed){
				if(currItem.equals("true")){
					list.set(i, true);
				}
				else if(currItem.equals("false")){
					list.set(i, false);
				}
				else if(((String) currItem).length() == 1){
					char charOfItem = ((String) currItem).charAt(0);
					list.set(i, charOfItem);
				}
			}
		}

		return list.toArray();
	}

	/**
	 * Trims class getName() from e.g. java.lang.String to String
	 * @param fullClass
	 * @return
	 */
	public static String getClassName(String fullClass){
		String classTitle = "";
		for (int i = 0; i < fullClass.length(); i++){
			char c = fullClass.charAt(i); 
			if(c == '.'){
				classTitle = "";
			} else {
				classTitle += c;
			}
		}
		return classTitle;
	}

}
