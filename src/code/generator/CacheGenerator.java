package code.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.NonCloneableBaseObject;

/**
 * 根据包名生成cache列表，默认配置到黑名单
 * 
 * @author Young
 * @since 2015.07.29
 */
public class CacheGenerator extends NonCloneableBaseObject {

	public static void main(String[] args) throws ClassNotFoundException {
		System.out.println("请输入完整包名路径:");
		Scanner sc = new Scanner(System.in);
		String packageName = sc.nextLine();
		sc.close();

		List<Class<?>> classes = getClasses(packageName);
		for (Class<?> clazz : classes) {
			if (clazz == CacheGenerator.class)
				continue;
			if (clazz.getSimpleName().endsWith("Action")) {
				System.out.println("[]cache_blacklist="
						+ clazz.getSimpleName().substring(0,
								clazz.getSimpleName().indexOf("Action")));
			} else {
				System.out
						.println("[]cache_blacklist=" + clazz.getSimpleName());
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