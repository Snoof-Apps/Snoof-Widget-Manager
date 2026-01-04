package snoof.widget.manager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the empty list first
        adapter = new WidgetAdapter(discoveredWidgets);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list every time the user sees this activity
        scanForWidgets();
    }

    private void scanForWidgets() {
        discoveredWidgets.clear();
        PackageManager pm = getPackageManager();

        // Query all installed applications
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            // Filter by prefix and exclude the manager itself
            if (packageInfo.packageName.startsWith("snoof.widget.") &&
                    !packageInfo.packageName.equals("snoof.widget.manager")) {

                String label = pm.getApplicationLabel(packageInfo).toString();
                Drawable icon = pm.getApplicationIcon(packageInfo);
                String pkgName = packageInfo.packageName;

                discoveredWidgets.add(new WidgetApp(label, pkgName, icon));
                Log.d("SnoofManager", "Found widget: " + label);
            }
        }

        // Notify the adapter that the data changed so it redraws the list
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        // Toggle visibility of the empty message
        if (discoveredWidgets.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    // Data model for our list items
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