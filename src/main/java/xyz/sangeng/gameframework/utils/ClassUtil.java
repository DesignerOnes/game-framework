package xyz.sangeng.gameframework.utils;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ClassUtil {

	private static ClassLoader bootstrapClassLoader = null;

	/**
	 * 取得某个接口下所有实现这个接口的类
	 * 
	 * @param c
	 * @return
	 */
	public static List<Class<?>> getAllClassByInterface(Class<?> c) {
		List<Class<?>> returnClassList = null;
		if (c.isInterface()) {
			String packageName = c.getPackage().getName();
			List<Class<?>> allClass = getClasses(packageName);
			if (allClass != null) {
				returnClassList = new ArrayList<>();
				for (Class<?> classes : allClass) {
					// 判断是否是同一个接口
					if (c.isAssignableFrom(classes)) {
						// 本身不加入进去
						if (!c.equals(classes)) {
							returnClassList.add(classes);
						}
					}
				}
			}
		}
		return returnClassList;
	}

	/**
	 * 取得所有的子类
	 * 
	 * @param baseClazz
	 * @param packageName 如果不传，默认传入的class所在的包
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> getAllSubClass(Class<T> baseClazz, String packageName) {
		List<Class<T>> returnClassList = null;
		if (StringUtils.isBlank(packageName)) {
			packageName = baseClazz.getPackage().getName();
		}
		// 获取当前包下以及子包下所有的类
		List<Class<?>> allClass = getClasses(packageName);
		if (allClass != null) {
			returnClassList = new ArrayList<>();
			for (Class<?> clazz : allClass) {
				// 判断是否是同一个接口
				if (baseClazz.isAssignableFrom(clazz)) {
					// 本身不加入进去
					if (!baseClazz.equals(clazz)) {
						returnClassList.add((Class<T>) clazz);
					}
				}
			}
		}
		return returnClassList;
	}

	/**
	 * 
	 * 从包package中获取所有的Class
	 * @param packageName
	 * @return
	 */
	public static List<Class<?>> getClasses(String packageName) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		//是否循环迭代
		boolean recursive = true;
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			if (null == bootstrapClassLoader) {
				bootstrapClassLoader = Thread.currentThread().getContextClassLoader();
			}
			dirs = bootstrapClassLoader.getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					//如果是以文件的形式保存在服务器上
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					//如果是jar包文件
					JarFile jar;
					try {
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							if (name.charAt(0) == '/') {
								name = name.substring(1);
							}
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								if (idx != -1) {
									packageName = name.substring(0, idx).replace('/', '.');
								}
								if ((idx != -1) || recursive) {
									if (name.endsWith(".class") && !entry.isDirectory()) {
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
		//获取此包的目录 建立一个File
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {

			//自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			//如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
						file.getAbsolutePath(),
						recursive,
						classes);
			} else {
				//如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					//添加到集合中去
					classes.add(Class.forName(packageName + '.' + className));
				} catch (Throwable e) {
					//GameLog.info(LogTypeSetBase.COMMON_RECORD_ERROR, packageName + '.' + className, e);
				}
			}
		}
	}
}
