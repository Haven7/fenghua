/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
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
import java.util.*;

/**
 * <Description/>
 *
 * @author zhangcg.
 * @Date 2019/3/22 - 4:55 PM.
 */
@SuppressWarnings("all")
public class HttpUtil {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String CHARSET = "UTF-8";
    private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
    private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpUtil().new TrustAnyHostnameVerifier();
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String get(String url) throws Exception {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, String> queryParas) throws Exception {
        return get(url, queryParas, null);
    }

    public static String get(String url, Map<String, String> queryParas, Map<String, String> headers) throws Exception {
        HttpURLConnection conn = null;
        String urlWithParams = buildUrlWithQueryString(url, queryParas);
        logger.info("开始get请求：{}", urlWithParams);
        conn = getHttpConnection(urlWithParams, GET, headers);
        try {
            conn.connect();
            String result = readResponseString(conn);
            logger.info("返回结果：{}", result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("900001");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String post(String url, String data) throws Exception {
        return post(url, null, data, null);
    }

    public static String post(String url, String data, Map<String, String> headers) throws Exception {
        return post(url, null, data, headers);
    }

    public static String post(String url, Map<String, String> queryParas, String data) throws Exception {
        return post(url, queryParas, data, null);
    }

    public static String post(String url, Map<String, String> queryParas, String data, Map<String, String> headers) throws Exception {
        HttpURLConnection conn = null;
        String urlWithParams = buildUrlWithQueryString(url, queryParas);
        logger.info("开始post请求：{}", urlWithParams);
        logger.info("body为：{}", data);
        conn = getHttpConnection(urlWithParams, POST, headers);
        try {
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes(CHARSET));
            out.flush();
            out.close();
            String result = readResponseString(conn);
            logger.info("返回结果：{}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static String getImageBase64FromNetByUrl(String strUrl, Map<String, String> queryParas, String data, Map<String, String> requestHeader) {
        HttpURLConnection conn = null;
        try {
            String urlWithParams = buildUrlWithQueryString(strUrl, queryParas);
            logger.info("开始get请求：{}", urlWithParams);
            conn = getHttpConnection(urlWithParams, GET, requestHeader);
            conn.connect();
            InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[10240];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            byte[] bytes = outStream.toByteArray();
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        //        try {
//            URL url = new URL(strUrl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(10 * 1000);
//            InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
//            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[10240];
//            int len = 0;
//            while ((len = inStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, len);
//            }
//            inStream.close();
//            byte[] bytes = outStream.toByteArray();
//            return Base64.encodeBase64String(bytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    /**
     * post 请求 上传图片
     *
     * @param path    请求路径
     * @param form    表单参数
     * @param fileMap 上传文件map
     * @param clazz   接收类型
     * @param <T>
     * @return
     */

    public static <T> ResponseEntity<T> post(RestTemplate restTemplate, String path, MultiValueMap<String, Object> form, Map<String, MultipartFile> fileMap, Class<T> clazz) {
        try {
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                form.add(entry.getKey(), getFileResource(entry.getValue()));
            }
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("multipart/form-data;charset=UTF-8");
            headers.setContentType(type);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);
            ResponseEntity<T> exchange = restTemplate.exchange(path, HttpMethod.POST, requestEntity, clazz);
            if (exchange.getStatusCode().value() == HttpStatus.OK.value()) {
                return exchange;
            } else {
                throw new RuntimeException("请求" + path + "异常！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

        conn.setConnectTimeout(19000);
        conn.setReadTimeout(19000);

        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return conn;
    }

    private static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
        if (queryParas == null || queryParas.isEmpty()) {
            return url;
        }

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
            TrustManager[] tm = {new HttpUtil().new TrustAnyTrustManager()};
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
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * https证书管理
     */
    private class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    /**
     * multipartFile to fileResource
     *
     * @param multipartFile
     * @return
     */
    private static ByteArrayResource getFileResource(final MultipartFile multipartFile) {
        ByteArrayResource fileResource = null;
        try {
            byte[] bytes = input2byte(multipartFile.getInputStream());
            fileResource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    String extensionName = getExtensionName(multipartFile.getOriginalFilename());
                    return UUID.randomUUID().toString().replaceAll("-", "") + "." + extensionName;
                }
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileResource;
    }

    /**
     * InputStream 转换为byte[]
     *
     * @param inStream
     * @return
     * @throws IOException
     */
    private static byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    private static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
}
