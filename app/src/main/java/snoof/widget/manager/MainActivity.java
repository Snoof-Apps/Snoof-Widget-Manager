package snoof.widget.manager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<WidgetApp> discoveredWidgets = new ArrayList<>();
    private RecyclerView recyclerView;
    private WidgetAdapter adapter;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WidgetAdapter(discoveredWidgets);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list using the signature intent scan
        scanForWidgets();
    }

    private void scanForWidgets() {
        discoveredWidgets.clear();
        PackageManager pm = getPackageManager();

        // We look for activities that respond to our "Secret Handshake" action
        Intent queryIntent = new Intent("snoof.widget.IDENTIFIER");

        // Query the system for apps matching this intent
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(queryIntent, 0);

        for (ResolveInfo info : resolveInfos) {
            // Get the ApplicationInfo from the activity that matched
            ApplicationInfo appInfo = info.activityInfo.applicationInfo;

            // Safety check to make sure we don't list the manager itself
            // (though the manager shouldn't have the IDENTIFIER intent filter anyway)
            if (!appInfo.packageName.equals(getPackageName())) {
                String label = pm.getApplicationLabel(appInfo).toString();
                Drawable icon = pm.getApplicationIcon(appInfo);
                String pkgName = appInfo.packageName;

                discoveredWidgets.add(new WidgetApp(label, pkgName, icon));
                Log.d("SnoofManager", "Found Signature Widget: " + label);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (discoveredWidgets.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    public static class WidgetApp {
        public String name;
        public String packageName;
        public Drawable icon;

        public WidgetApp(String name, String packageName, Drawable icon) {
            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
        }
    }
}