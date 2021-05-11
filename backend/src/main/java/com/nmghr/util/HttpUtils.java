package com.nmghr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;


public class HttpUtils {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String CHARSET = "UTF-8";
    private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
    private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpUtils().new TrustAnyHostnameVerifier();
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, String> queryParas) {
        return get(url, queryParas, null);
    }

    public static String get(String url, Map<String, String> queryParas, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            String urlWithParams = buildUrlWithQueryString(url, queryParas);
            logger.info("开始get请求：{}", urlWithParams);
            conn = getHttpConnection(urlWithParams, GET, headers);
            conn.connect();
            String result = readResponseString(conn);
            logger.info("返回结果：{}", result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String post(String url, String data) {
        return post(url, null, data, null);
    }

    public static String post(String url, String data, Map<String, String> headers) {
        return post(url, null, data, headers);
    }

    public static String post(String url, Map<String, String> queryParas, String data) {
        return post(url, queryParas, data, null);
    }

    public static String post(String url, Map<String, String> queryParas, String data, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            String urlWithParams = buildUrlWithQueryString(url, queryParas);
            logger.info("开始post请求：{}", urlWithParams);
            logger.info("body为：{}", data);
            conn = getHttpConnection(urlWithParams, POST, headers);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes(CHARSET));
            out.flush();
            out.close();
            String result = readResponseString(conn);
            logger.info("返回结果：{}", result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 下载文件
     *
     * @param url                 url
     * @param filePath            文件保存路径，一般为tomcat的webapp文件夹路径
     * @param pathPrefix          相对路径的前缀
     * @param queryParas          url中的参数
     * @param searchFilenameIndex 查询"Content-disposition"中filename下标的匹配字符串
     * @param fileSuffix          文件后缀名（当filename中不包含后缀时生效）
     * @return 文件存储的相对位置
     */
    public static String downloadFile(String url, String filePath, String pathPrefix, Map<String, String> queryParas, String searchFilenameIndex, String fileSuffix) {
        HttpURLConnection conn = null;
        FileOutputStream fileOutputStream = null;
        try {
            String urlWithParams = buildUrlWithQueryString(url, queryParas);
            logger.info("开始get请求下载素材文件：{}", urlWithParams);
            conn = getHttpConnection(urlWithParams, GET, null);
            conn.connect();
            // 获取文件名filename
            String headerField = conn.getHeaderField("Content-disposition");
            String fileName = headerField.substring(headerField.indexOf(searchFilenameIndex) + searchFilenameIndex.length(), headerField.length() - 1);
            if (!ObjectUtils.isEmpty(fileName) && !fileName.contains(".")) {
                fileName += fileSuffix;
            }
            logger.info("素材文件名：{}", fileName);
            InputStream inputStream = conn.getInputStream();
            String folderName = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String localUrl = File.separator + folderName + File.separator + fileName;
            String localFolder = File.separator + folderName;
            if (StringUtils.hasText(pathPrefix)) {
                localUrl = File.separator + pathPrefix + localUrl;
                localFolder = File.separator + pathPrefix + localFolder;
            }
            File folder = new File(filePath + localFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            fileOutputStream = new FileOutputStream(new File(filePath + localUrl));
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileOutputStream.close();
            logger.info("素材文件保存路径：{}", filePath + localUrl);
            return localUrl.replace("\\", "/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void downloadFile(String url, HttpServletResponse response) {
        HttpURLConnection conn = null;
        ServletOutputStream os = null;
        try {
            logger.info("开始get请求下载素材文件：{}", url);
            conn = getHttpConnection(url, GET, null);
            conn.connect();
            response.addHeader("content-type", conn.getHeaderField("content-type"));
            BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
            os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = bin.read(buffer)) != -1) {
                os.write(buffer, 0, byteRead);
            }
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载文件
     *
     * @param url        url
     * @param filePath   文件保存路径，一般为tomcat的webapp文件夹路径
     * @param pathPrefix 相对路径的前缀
     * @param queryParas url中的参数
     * @return 文件存储的相对位置
     */
    public static String downloadFile(String url, String filePath, String pathPrefix, Map<String, String> queryParas) {
        return downloadFile(url, filePath, pathPrefix, queryParas, "filename=\"", "");
    }

    private static HttpURLConnection getHttpConnection(String url, String method, Map<String, String> headers)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        URL _url = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
        }

        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setConnectTimeout(190000);
        conn.setReadTimeout(190000);

        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

        if (headers != null && !headers.isEmpty())
            for (Map.Entry<String, String> entry : headers.entrySet())
                conn.setRequestProperty(entry.getKey(), entry.getValue());

        return conn;
    }

    private static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
        if (queryParas == null || queryParas.isEmpty())
            return url;

        StringBuilder sb = new StringBuilder(url);
        boolean isFirst;
        if (!url.contains("?")) {
            isFirst = true;
            sb.append("?");
        } else {
            isFirst = false;
        }

        for (Map.Entry<String, String> entry : queryParas.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append("&");
            }

            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.hasText(value)) {
                try {
                    value = URLEncoder.encode(value, CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }

    private static String readResponseString(HttpURLConnection conn) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static SSLSocketFactory initSSLSocketFactory() {
        try {
            TrustManager[] tm = {new HttpUtils().new TrustAnyTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String str) {
        try {
            return URLDecoder.decode(str, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readIncommingRequestData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line = null; (line = br.readLine()) != null; ) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 取得带相同默认前缀"search_"的Request Parameters
     *
     * @param request ServletRequest
     * @return 返回的Parameter名已去除前缀
     */
    public static Map<String, Object> getParameters(ServletRequest request) {
        return getParametersStartingWith(request, "search_");
    }

    /**
     * 取得带相同前缀的Request Parameters
     *
     * @param request ServletRequest
     * @param prefix  前缀
     * @return 返回的Parameter名已去除前缀
     */
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while ((paramNames != null) && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unPrefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if ((values == null) || (values.length == 0)) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unPrefixed, values);
                } else {
                    try {
                        params.put(unPrefixed, URLDecoder.decode(values[0], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        params.put(unPrefixed, values[0]);
                    }
                }
            }
        }
        return params;
    }

    /**
     * 从request里取参数name的值
     *
     * @param name    name
     * @param request HttpServletRequest
     * @return value
     */
    public static String getPara(String name, HttpServletRequest request) {
        return request.getParameter(name);
    }

    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * https域名校验
     */
    private class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * https证书管理
     */
    private class TrustAnyTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    /**
     * 下载文件并监听下载进度
     *
     * @param url
     * @param request
     * @param response
     */
    public static void downloadFile(String url, HttpServletRequest request, HttpServletResponse response) {
        HttpURLConnection conn = null;
        ServletOutputStream os = null;
        try {
            logger.info("开始get请求下载素材文件：{}", url);
            conn = getHttpConnection(url, GET, null);
            conn.connect();
            response.addHeader("content-type", conn.getHeaderField("content-type"));
            BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());

            // 整体总数
            int total = conn.getContentLength();
            logger.info("待下载的文件大小：{}", total);
            // 当前总数
            int currentTotal = 0;
            HttpSession session = request.getSession();
            session.setAttribute("total", total);

            os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = bin.read(buffer)) != -1) {
                // 计算出当前的下载进度
                currentTotal += byteRead;
                session.setAttribute("currentTotal", currentTotal);
                // 输出流执行写的操作
                os.write(buffer, 0, byteRead);
            }
            os.flush();
            logger.info("已下载的文件大小：{}", currentTotal);
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
