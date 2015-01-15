import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import o2hen2.Service;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xml.sax.InputSource;

public class Programm {

	static class MyClassVisitor extends ClassVisitor {

		public MyClassVisitor() {
			super(Opcodes.ASM5);
		}

		@Override
		public void visit(int arg0, int arg1, String arg2, String arg3,
				String arg4, String[] arg5) {
			// TODO Auto-generated method stub
			super.visit(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String name, boolean arg1) {
			
			if (name.equals(Type.getType(Service.class).getDescriptor())){
				System.err.println("annotation presend");
			}
			
			return null;
		}

	}

	public static List<URL> getUrls() {
		List<URL> urls = new ArrayList<URL>();

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		while (classLoader != null) {
			if (classLoader instanceof URLClassLoader) {
				urls.addAll(Arrays.asList(((URLClassLoader) classLoader)
						.getURLs()));
			}
			classLoader = classLoader.getParent();
		}

		return urls;
	}

	public static void scanDirectory(Path directory) {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				if (Files.isDirectory(path)) {
					scanDirectory(path);
				} else {
					try (InputStream stream =  new BufferedInputStream(Files.newInputStream(path))) {
						ClassReader classReader = new ClassReader(stream);
						classReader.accept(new MyClassVisitor(), 0);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} 		
	}

	public static void main(String[] args) {
		
		
		for (URL url : Programm.getUrls()) {
			
			Path path = null;
			try {
				path = Paths.get(url.toURI());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (path != null && Files.isDirectory(path)) {
				scanDirectory(path);
			}
		}

	}

}
