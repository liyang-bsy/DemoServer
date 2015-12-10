package code.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.NonCloneableBaseObject;

/**
 * 根据包名生成action列表，会根据类的后缀决定是否补上"Action"字样，如 <br>
 * TestAction=xxx.xxx.xxx.Test <br>
 * TestAction=xxx.xxx.xxx.TestAction
 * 
 * @author Young
 * @since 2015.07.29
 */
public class ActionGenerator extends NonCloneableBaseObject {

	public static void main(String[] args) throws ClassNotFoundException {
		System.out.println("请输入完整包名路径:");
		Scanner sc = new Scanner(System.in);
		String packageName = sc.nextLine();
		sc.close();

		List<Class<?>> classes = getClasses(packageName);
		for (Class<?> clazz : classes) {
			if (clazz == ActionGenerator.class)
				continue;
			if (clazz.getSimpleName().endsWith("Action")) {
				System.out.println(clazz.getSimpleName() + "="
						+ clazz.getName());
			} else {
				 System.out.println(clazz.getSimpleName() + "Action" + "=" +
				 clazz.getName());
			}
		}
	}

	public static List<Class<?>> getClasses(String packageName)
			throws ClassNotFoundException {
		String filePath = CoreDef.rootPath + "\\src\\"
				+ packageName.replace(".", "\\");
		List<Class<?>> fileNames = getClasses(filePath, null);
		return fileNames;
	}

	private static List<Class<?>> getClasses(String filePath,
			List<Class<?>> className) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				classes.addAll(getClasses(childFile.getPath(), classes));
			} else {
				String childFilePath = childFile.getPath();
				childFilePath = childFilePath.substring(
						(CoreDef.rootPath + "\\src\\").length(),
						childFilePath.lastIndexOf("."));
				childFilePath = childFilePath.replace("\\", ".");
				classes.add(Class.forName(childFilePath));
			}
		}
		return classes;
	}
}