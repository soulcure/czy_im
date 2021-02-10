package com.youmai.hxsdk.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * Created by colin on 2017/6/6.
 * okhttp utils
 */

public class OkHttpConnector {
    private static final String TAG = OkHttpConnector.class.getSimpleName();

    //private static final OkHttpClient client = new OkHttpClient.Builder().build();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor(new StethoInterceptor())  //添加拦截器
            .build();

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    private static final MediaType MEDIA_TYPE_PNG
            = MediaType.parse("image/png");


    private OkHttpConnector() {
        throw new AssertionError();
    }

    /**
     * AsyncTask to get data by HttpURLConnection
     * 2016-9-13
     */

    public static void httpGet(String url, IGetListener request) {
        httpGet(null, url, null, request);
    }


    public static void httpGet(ContentValues header, String url,
                               IGetListener request) {
        httpGet(header, url, null, request);
    }

    public static void httpGet(String url, ContentValues params,
                               IGetListener request) {
        httpGet(null, url, params, request);
    }


    public static void httpGet(ContentValues headers, String url,
                               ContentValues params, IGetListener request) {
        HttpGetAsyncTask task = new HttpGetAsyncTask(request);
        task.setParams(params);
        task.setHeaders(headers);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

    }


    public static String doGet(ContentValues headers, String url, ContentValues params) {
        String res;
        try {
            if (params != null && params.size() > 0) {
                url = url + "?" + getParams(params, true);
            }

            Request.Builder builder = new Request.Builder();
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, Object> entry : headers.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    Log.v(TAG, "okhttp Header:" + key + "=" + value);
                    builder.addHeader(key, value);
                }
            }

            Log.v(TAG, "okhttp get url:" + url);

            Request request = builder.url(url).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                res = response.body().string();
            } else {
                res = response.toString();
            }
        } catch (IOException e) {
            res = null;
        }
        return res;
    }


    public static void httpPost(ContentValues header, String url,
                                IPostListener request) {
        httpPost(url, null, header, null, request);
    }


    public static void httpPost(String url, ContentValues params,
                                IPostListener request) {
        httpPost(url, params, null, null, request);
    }

    public static void httpPost(ContentValues header, String url,
                                ContentValues params, IPostListener request) {
        httpPost(url, params, header, null, request);
    }


    public static void httpPost(String url, String postBoby,
                                IPostListener request) {
        httpPost(url, null, null, postBoby, request);
    }

    public static void httpPost(String url, ContentValues params,
                                ContentValues headers, String postBoby,
                                IPostListener request) {
        HttpPostAsyncTask task = new HttpPostAsyncTask(request);
        task.setParams(params);
        task.setHeaders(headers);
        task.setPostBoby(postBoby);
        task.execute(url);
    }


    /**
     * 多表单混合提交
     *
     * @param url
     * @param params
     * @param request
     */
    public static void httpPostMultipart(String url, Map<String, Object> params,
                                         IPostListener request) {
        httpPostMultipart(url, null, params, request);
    }


    /**
     * 多表单混合提交
     *
     * @param url
     * @param headers
     * @param params
     * @param request
     */
    public static void httpPostMultipart(String url, ContentValues headers, Map<String, Object> params,
                                         IPostListener request) {
        HttpPostMultipartAsyncTask task = new HttpPostMultipartAsyncTask(request);
        task.setHeaders(headers);
        task.setParams(params);
        task.execute(url);
    }


    /**
     * 下载文件
     *
     * @param url
     * @param md5
     * @param filePath
     * @param fileName
     * @param listener
     */
    public static void httpDownload(String url, String md5, String filePath,
                                    String fileName, DownloadListener listener) {

        HttpDownLoadFileAsyncTask task = new HttpDownLoadFileAsyncTask(listener);
        task.setMd5(md5);
        task.setFilePath(filePath);
        task.setFileName(fileName);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }


    private static class HttpGetAsyncTask extends
            AsyncTask<String, Void, String> {

        private IGetListener mRequest;
        private ContentValues mParams;
        private ContentValues mHeaders;

        public HttpGetAsyncTask(IGetListener request) {
            mRequest = request;
        }

        public void setHeaders(ContentValues headers) {
            mHeaders = headers;
        }


        public void setParams(ContentValues headers) {
            mParams = headers;
        }


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url is null");
            }

            try {
                if (mParams != null && mParams.size() > 0) {
                    url = url + "?" + getParams(mParams, true);
                }

                Request.Builder builder = new Request.Builder();
                if (mHeaders != null && mHeaders.size() > 0) {
                    for (Map.Entry<String, Object> entry : mHeaders.valueSet()) {
                        String key = entry.getKey(); // name
                        String value = entry.getValue().toString(); // value
                        Log.v(TAG, "okhttp Header:" + key + "=" + value);
                        builder.addHeader(key, value);
                    }
                }

                Log.v(TAG, "okhttp get url:" + url);

                Request request = builder.url(url).build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return response.toString();
                }

            } catch (IOException e) {
                return e.toString();
            }

        }

        @Override
        protected void onPostExecute(String response) {
            Log.v(TAG, "okhttp get response body:" + response);
            if (mRequest != null) {
                mRequest.httpReqResult(response);
            }

        }

    }

    private static class HttpPostAsyncTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;
        private ContentValues mParams;
        private ContentValues mHeaders;
        private String mPostBoby;


        public HttpPostAsyncTask(IPostListener request) {
            mRequest = request;
        }

        public void setParams(ContentValues headers) {
            mParams = headers;
        }


        public void setHeaders(ContentValues headers) {
            mHeaders = headers;
        }

        public void setPostBoby(String postBoby) {
            this.mPostBoby = postBoby;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url is null");
            }
            RequestBody body = null;
            if (mParams != null && mParams.size() > 0) {
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, Object> entry : mParams.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    builder.add(key, value);
                }
                body = builder.build();
            } else if (!TextUtils.isEmpty(mPostBoby)) {
                body = RequestBody.create(JSON, mPostBoby);
            } else {
                body = RequestBody.create(null, "");
            }

            Request.Builder builder = new Request.Builder();
            if (mHeaders != null && mHeaders.size() > 0) {
                for (Map.Entry<String, Object> entry : mHeaders.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    Log.v(TAG, "okhttp Header:" + key + "=" + value);
                    builder.addHeader(key, value);
                }
            }

            Log.v(TAG, "okhttp post url:" + url);
            Request request = builder.url(url).post(body).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return response.toString();
                }

            } catch (IOException e) {
                return e.toString();
            }

        }

        @Override
        protected void onPostExecute(String response) {
            Log.v(TAG, "okhttp post response:" + response);
            if (mRequest != null) {
                mRequest.httpReqResult(response);
            }
        }
    }


    /**
     * 表单提交
     */
    private static class HttpPostMultipartAsyncTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;
        private Map<String, Object> mParams;
        private ContentValues mHeaders;

        private HttpPostMultipartAsyncTask(IPostListener request) {
            mRequest = request;
        }

        private void setParams(Map<String, Object> params) {
            this.mParams = params;
        }

        private void setHeaders(ContentValues headers) {
            this.mHeaders = headers;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url is null");
            }

            MultipartBody.Builder multiBuilder = new MultipartBody.Builder();

            //设置类型
            multiBuilder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : mParams.keySet()) {
                Object object = mParams.get(key);
                if ((object instanceof File)) {
                    File file = (File) object;
                    multiBuilder.addFormDataPart(key, file.getName(),
                            RequestBody.create(MEDIA_TYPE_PNG, file));
                } else {
                    multiBuilder.addFormDataPart(key, object.toString());
                }
            }
            //创建RequestBody
            RequestBody body = multiBuilder.build();


            Request.Builder builder = new Request.Builder();
            if (mHeaders != null && mHeaders.size() > 0) {
                for (Map.Entry<String, Object> entry : mHeaders.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    Log.v(TAG, "okhttp Header:" + key + "=" + value);
                    builder.addHeader(key, value);
                }
            }

            Log.v(TAG, "okhttp post multipart/form-data url:" + url);
            Request request = builder.url(url).post(body).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return response.toString();
                }

            } catch (IOException e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(String response) {
            Log.v(TAG, "okhttp post multipart/form-data response:" + response);
            if (mRequest != null) {
                mRequest.httpReqResult(response);
            }
        }

    }


    /**
     * 上传文件
     */
    public static class HttpPostFileAsyncTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;
        private File mFile;

        public HttpPostFileAsyncTask(IPostListener request) {
            mRequest = request;
        }

        public void setFile(File file) {
            this.mFile = file;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url is null");
            }

            Log.v(TAG, "okhttp post file url:" + url);
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, mFile))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return response.toString();
                }

            } catch (IOException e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(String response) {
            Log.v(TAG, "okhttp post response:" + response);
            if (mRequest != null) {
                mRequest.httpReqResult(response);
            }
        }


    }


    /**
     * 上传流 (支持更新进度)
     */
    public static class HttpPostStreamAsyncTask extends
            AsyncTask<String, Integer, String> {

        private DownloadListener mProgressListener;
        private IPostListener mRequest;
        private File mFile;  //demo is txt file

        public HttpPostStreamAsyncTask(IPostListener request) {
            mRequest = request;
        }

        public void setFile(File file) {
            this.mFile = file;
        }

        public void setProgressListener(DownloadListener progressListener) {
            mProgressListener = progressListener;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url is null");
            }
            RequestBody requestBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MEDIA_TYPE_MARKDOWN;
                }

                @Override
                public long contentLength() {
                    return mFile.length();
                }

                @Override
                public void writeTo(BufferedSink sink) {
                    Source source;
                    try {
                        source = Okio.source(mFile);
                        Buffer buf = new Buffer();
                        long remaining = contentLength();
                        long current = 0;
                        long readCount;
                        while ((readCount = source.read(buf, 2048)) != -1) {
                            sink.write(buf, readCount);
                            current += readCount;
                            onProgressUpdate((int) current, (int) remaining);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Log.v(TAG, "okhttp post stream url:" + url);
            Request request = new Request.Builder().url(url).post(requestBody).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return response.toString();
                }

            } catch (IOException e) {
                return e.toString();
            }


        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            int cur = progress[0].intValue();
            int total = progress[1].intValue();
            if (mProgressListener != null)
                mProgressListener.onProgress(cur, total);

        }


        @Override
        protected void onPostExecute(String response) {
            Log.v(TAG, "okhttp post steam response:" + response);
            if (mRequest != null) {
                mRequest.httpReqResult(response);
            }

        }


    }


    /**
     * 下载文件
     */
    private static class HttpDownLoadFileAsyncTask extends
            AsyncTask<String, Integer, String> {

        private DownloadListener mProgressListener;
        private String md5;//下载文件的md5
        private String filePath;
        private String fileName;

        private HttpDownLoadFileAsyncTask(DownloadListener listener) {
            mProgressListener = listener;
        }

        private void setMd5(String md5) {
            this.md5 = md5;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url is null");
            }

            Log.v(TAG, "okhttp download url:" + url);

            if (TextUtils.isEmpty(fileName)) {
                fileName = AppUtils.md5(url);
            }
            if (TextUtils.isEmpty(filePath)) {
                filePath = FileConfig.getFileDownLoadPath();
            }
            final File file = new File(filePath, fileName);
            File tmpFile;
            if (file.exists()) {
                return file.getPath();
            } else {
                // 设置下载的临时文件名
                tmpFile = new File(FileConfig.getFileDownLoadPath(), fileName + ".tmp");
            }
            long startPosition = tmpFile.length(); // 已下载的文件长度

            try {
                Request request = new Request.Builder()
                        .addHeader("RANGE", "bytes=" + startPosition + "-")  //断点续传要用到的，指示下载的区间
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                long contentLength = body.contentLength();

                BufferedSource source = body.source();

                BufferedSink sink = Okio.buffer(Okio.sink(tmpFile));

                long totalRead = 0;
                long read = 0;
                while ((read = source.read(sink.buffer(), 2048)) != -1) {
                    totalRead += read;
                    publishProgress((int) totalRead, (int) contentLength);
                }
                sink.writeAll(source);
                sink.flush();
                sink.close();
            } catch (IOException e) {
                mProgressListener.onFail(e.toString());
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
                return null;
            }
            if (!TextUtils.isEmpty(md5)) {
                // 下载文件MD5检测
                if (AppUtils.checkFileMd5(tmpFile, md5)) {
                    Log.v(TAG, "download file md5 check success");
                    tmpFile.renameTo(file);
                } else {
                    Log.e(TAG, "download file md5 check fail");
                    tmpFile.delete();
                    return null;

                }
            } else {
                Log.v(TAG, "download file md5 check success");
                tmpFile.renameTo(file);
            }

            return file.getPath();
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            int cur = progress[0].intValue();
            int total = progress[1].intValue();

            mProgressListener.onProgress(cur, total);

        }


        @Override
        protected void onPostExecute(String strPath) {
            if (strPath != null) {
                mProgressListener.onSuccess(strPath);
            }
        }


    }


    /**
     * 组装参数
     *
     * @param params
     * @param isEncoder
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getParams(ContentValues params, boolean isEncoder) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : params.valueSet()) {
            String key = entry.getKey(); // name
            String value = entry.getValue().toString(); // value
            if (first) {
                first = false;
                //sb.append("?");
            } else {
                sb.append("&");
            }

            if (isEncoder) {
                sb.append(URLEncoder.encode(key, "UTF-8"));
            } else {
                sb.append(key);
            }

            sb.append("=");

            if (isEncoder) {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } else {
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
