package net.lylab.vicp.web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import flexjson.JSONDeserializer;

public class HttpUtils {
	/**
	 * 描述：sendUrl 发送URL的post请求
	 * 
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public static String sendPost(String urlStr, String params) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(urlStr);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			conn.setConnectTimeout(60 * 1000);
			conn.setReadTimeout(60 * 1000);
			// 设置通用的请求属性
			// conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// conn.setRequestProperty("accept", "*/*");
			// conn.setRequestProperty("connection", "Keep-Alive");
			// conn.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 发送请求参数
			out.write(params);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("网络通讯失败，url=" + urlStr + "\n参数=" + params);
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	/**
	 * 描述：saveAs Save Internet resource to local, accept
	 * FTP(anonymous)/HTTP/HTTPS protocol.
	 * 
	 * @param sUrl
	 * @param sPath
	 * @return 0 success
	 * @return -1 fail Example:
	 *         HttpUtils.saveAs("https://.../image/banner.jpg","/Users/.../Desktop/"
	 *         );
	 */
	public static int saveAs(String sUrl, String sPath) {
		try {
			URL url;
			url = new URL(sUrl);
			int lastChar;
			for (lastChar = sUrl.length() - 1; lastChar >= 0; lastChar--) {
				if (sUrl.charAt(lastChar) == '\\'
						|| sUrl.charAt(lastChar) == '/')
					break;
			}
			String fileName = sUrl.substring(lastChar + 1);
			File outFile = new File(sPath + fileName);
			OutputStream os = new FileOutputStream(outFile);
			InputStream is = url.openStream();
			byte[] buff = new byte[1024];
			while (true) {
				int readed = is.read(buff);
				if (readed == -1) {
					break;
				}
				byte[] temp = new byte[readed];
				System.arraycopy(buff, 0, temp, 0, readed);
				os.write(temp);
			}
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public static String reqShortUrl(String url) {
		return url;
		// return reqShortUrlFromSina(url);
	}

	public static String reqShortUrlFromGoogle(String url) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(
					"https://www.googleapis.com/urlshortener/v1/url");
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print("{\"longUrl\":\"" + url + "\"}");
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Map<String, String> map = null;
		JSONDeserializer<Map<String, String>> des = new JSONDeserializer<Map<String, String>>();
		map = (Map<String, String>) des.deserialize(result.toString());
		return map.get("id");
	}

	public static String reqShortUrlFromSina(String urlStr) {
		try {
			URL url = new URL(
					"http://api.t.sina.com.cn/short_url/shorten.json?source=209678993&url_long="
							+ urlStr);
			InputStream in = url.openStream();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in,
					"utf-8"));
			String s = null;
			String result = null;
			while ((s = bin.readLine()) != null) {
				result += s;
			}
			bin.close();
			if (result.startsWith("null"))
				result = result.substring(4);
			List<Map<String, String>> lm = null;
			JSONDeserializer<List<Map<String, String>>> des = new JSONDeserializer<List<Map<String, String>>>();
			lm = (List<Map<String, String>>) des.deserialize(result.toString());
			return lm.get(0).get("url_short");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlStr;
	}

	public static String sendGet(String urlStr) {
		try {
			URL url = new URL(urlStr);
			InputStream in = url.openStream();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in,
					"utf-8"));
			String s = null;
			String result = null;
			while ((s = bin.readLine()) != null) {
				result += s;
			}
			bin.close();
			if (result.startsWith("null"))
				result = result.substring(4);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlStr;
	}

	/**
	 * 描述：sendGetWithTimeout 发送URL的get请求并携带超时限制
	 * 
	 * @param urlStr
	 * @param timeout
	 * @return
	 */
	public static String sendGetWithTimeout(String urlStr, Integer timeout) {
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(urlStr);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			((HttpURLConnection) conn).setRequestMethod("GET");
			conn.setReadTimeout(timeout.intValue());
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}
}
