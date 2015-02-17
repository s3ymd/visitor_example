
import java.util.LinkedList;
import java.util.List;

/////////////////////////////////////////////////////////////

interface Visitor {
	void visit(File file);
	void visit(Folder folder);
}

interface Element {
	void accept(Visitor visitor);
}

class ListVisitor implements Visitor {
	private int level = 0;
	@Override
	public void visit(File file) {
		printIndent();
		System.out.println("ファイル:" + file.getName());
	}

	@Override
	public void visit(Folder folder) {
		printIndent();
		System.out.println("フォルダ:" + folder.getName());
		level++;
		for (Entry e: folder.getEntries()) {
			e.accept(this);
		}
		level--;
	}
	private void printIndent() {
		for (int i = 0; i < level; i++) {
			System.out.print("\t");
		}
	}
}

class TotalSizeVisitor implements Visitor {
	private int totalSize = 0;
	@Override
	public void visit(File file) {
		totalSize += file.getSize();
	}

	@Override
	public void visit(Folder folder) {
		for (Entry e: folder.getEntries()) {
			e.accept(this);
		}
	}

	public int getTotalSize() {
		return totalSize;
	}
}
/////////////////////////////////////////////////////////////

abstract class Entry implements Element {
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract int getSize();
}

class Folder extends Entry {
	private List<Entry> entries = new LinkedList<>();
	public Folder(String name) {
		setName(name);
	}
	public List<Entry> getEntries() {
		return entries;
	}
	public void add(Entry entry) {
		entries.add(entry);
	}

	public int getSize() {
		int sum = 0;
		for (Entry e : entries) {
			sum += e.getSize();
		}
		return sum;

		// Java8のStreamを使用すると以下のように書くこともできる
		// return entries.stream().mapToInt(Entry::getSize).sum();
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}

class File extends Entry {
	private int size;

	public File(String name, int size) {
		setName(name);
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

public class Main {
	public static void main(String[] args) {
		Folder a = new Folder("a");
		Folder b = new Folder("b");
		Folder c = new Folder("c");

		File d = new File("d", 100);
		File e = new File("e", 100);
		File f = new File("f", 100);
		File g = new File("g", 100);
		File h = new File("h", 100);
		// フォルダbに、ファイルd, eを追加
		b.add(d);
		b.add(e);
		// フォルダcに、ファイルf, g, hを追加
		c.add(f);
		c.add(g);
		c.add(h);
		// フォルダaに、フォルダb, cを追加
		a.add(b);
		a.add(c);

		ListVisitor visitor = new ListVisitor();
		visitor.visit(a);
		TotalSizeVisitor visitor2 = new TotalSizeVisitor();
		visitor2.visit(a);
		System.out.println(visitor2.getTotalSize());
	}
}
