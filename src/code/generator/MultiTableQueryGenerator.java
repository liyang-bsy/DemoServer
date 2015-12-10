package code.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.vicp.lylab.core.NonCloneableBaseObject;

/**
 * mybatis用的自动生成多对多关系，多表连查列表（带分页）的基本sql语句
 * <br>需要输入大量信息，不太好用
 * <br>但是如果自己写，也挺蛋疼
 * 
 * @author Young
 * @since 2015.07.31
 */
public class MultiTableQueryGenerator extends NonCloneableBaseObject {

	public static void main(String[] args) throws ClassNotFoundException {
		try (Scanner sc = new Scanner(System.in);) {
			System.out.println("请输入主表");
			String mainTable = sc.nextLine().replaceAll("[\u0000-\u0020]", "");
			System.out.println("请输入主表字段列表，英文半角逗号分隔");
			String mainColumns = sc.nextLine().replaceAll("[\u0000-\u0020]", "");
			System.out.println("请输入主表分页条件");
			String pageColumn = sc.nextLine().replaceAll("[\u0000-\u0020]", "");

			List<String> relTables = new ArrayList<String>();
			List<String> relTableColumns = new ArrayList<String>();
			List<String> relMidTables = new ArrayList<String>();
			List<String> relConnectColumnToMainTable = new ArrayList<String>();
			List<String> relConnectColumn = new ArrayList<String>();

			System.out.println("请输入关联表数量");
			int relCnt = Integer.valueOf(sc.nextLine());
			while (relCnt-- > 0) {
				System.out.println("请输入关联表");
				relTables.add(sc.nextLine().replaceAll("[\u0000-\u0020]", ""));
				System.out.println("请输入关联表字段列表，英文半角逗号分隔");
				relTableColumns.add(sc.nextLine().replaceAll("[\u0000-\u0020]", ""));
				System.out.println("请输入中间表");
				relMidTables.add(sc.nextLine().replaceAll("[\u0000-\u0020]", ""));
				System.out.println("请输入中间表与主表的关联字段");
				relConnectColumnToMainTable.add(sc.nextLine().replaceAll("[\u0000-\u0020]", ""));
				System.out.println("请输入中间表与关联表的关联字段");
				relConnectColumn.add(sc.nextLine().replaceAll("[\u0000-\u0020]", ""));
			}
			StringBuilder sql = new StringBuilder("");

			// select 部分
			sql.append("select\n\t  ");
			String table = mainTable, columns = mainColumns;
			int curSelectTable = -1;
			while (true) {
				boolean first = true;
				int lineCnt = 0;
				for (String column : columns.split("\\,")) {
					if (lineCnt > 100) {
						sql.append("\n\t");
						lineCnt = 0;
					}
					if (first) {
						first = false;
						if (curSelectTable != -1)
							sql.append("\n\n\t, ");
					} else
						sql.append(", ");
					int before = sql.length();
					sql.append(table);
					sql.append(".");
					sql.append(column);
					sql.append(" as ");
					sql.append(column);
					int after = sql.length();
					lineCnt += after-before;
				}
				curSelectTable++;
				if (curSelectTable >= relTables.size())
					break;
				table = relTables.get(curSelectTable);
				columns = relTableColumns.get(curSelectTable);
			}
			;
			// from xxx
			sql.append("\n\nfrom " + mainTable);

			// left join部分
			String relTable, midTable, connectColumn, connectColumnToMainTable;

			boolean first = true;
			for (int curRelTable = 0; curRelTable < relTables.size(); curRelTable++) {
				relTable = relTables.get(curRelTable);
				midTable = relMidTables.get(curRelTable);
				connectColumn = relConnectColumn.get(curRelTable);
				connectColumnToMainTable = relConnectColumnToMainTable.get(curRelTable);
				
				if (first) {
					first = false;
					sql.append("\n");
				} else
					sql.append("\n\n");
				sql.append("\tleft join ");
				sql.append(midTable);
				sql.append(" ");
				sql.append(midTable);
				sql.append("_connect");
				sql.append(" on ");
				sql.append(mainTable);
				sql.append(".");
				sql.append(connectColumnToMainTable);
				sql.append("=");
				sql.append(midTable);
				sql.append("_connect");
				sql.append(".");
				sql.append(connectColumnToMainTable);

				sql.append("\n");
				sql.append("\tleft join ");
				sql.append(relTable);
				sql.append(" on ");
				sql.append(relTable);
				sql.append(".");
				sql.append(connectColumn);
				sql.append("=");
				sql.append(midTable);
				sql.append("_connect");
				sql.append(".");
				sql.append(connectColumn);
			}

		    // where jk_disease.dis_id in
			// (select dis_id from (select main.dis_id from jk_disease main order by dis_id limit 5)A)
		    sql.append("\nwhere ");
		    sql.append(mainTable);
		    sql.append(".");
		    sql.append(pageColumn);
			sql.append(" in (select ");
		    sql.append(pageColumn);
			sql.append(" from (select ");
		    sql.append(pageColumn);
			sql.append(" from ");
		    sql.append(mainTable);
		    sql.append(" order by ");
		    sql.append(pageColumn);
		    sql.append(" limit #{page.index},#{page.pageSize})_NOUSE1)");

			sql.append("\n;");
			System.out.println(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}