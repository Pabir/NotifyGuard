package com.pabirul.notifyguard;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import java.util.HashSet;
import java.util.Set;

public class BlockedUrlsFetcher {

    public interface BlockedUrlsListener {
        void onBlockedUrlsFetched(Set<String> blockedUrls);
    }

    public static void fetchBlockedUrls(Context context, BlockedUrlsListener listener) {
        new FetchBlockedUrlsTask(listener).execute();
    }

    private static class FetchBlockedUrlsTask extends AsyncTask<Void, Void, Set<String>> {
        private static final String BLOCKED_URLS_API = "https://raw.githubusercontent.com/Pabir/website-urls-api/main/urls.json";
        private BlockedUrlsListener listener;

        public FetchBlockedUrlsTask(BlockedUrlsListener listener) {
            this.listener = listener;
        }

        @Override
        protected Set<String> doInBackground(Void... voids) {
            Set<String> blockedUrls = new HashSet<>();
            try {
                URL url = new URL(BLOCKED_URLS_API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    blockedUrls.add(jsonArray.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return blockedUrls;
        }

        @Override
        protected void onPostExecute(Set<String> blockedUrls) {
            if (listener != null) {
                listener.onBlockedUrlsFetched(blockedUrls);
            }
        }
    }
}
