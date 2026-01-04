package snoof.widget.manager;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WidgetAdapter extends RecyclerView.Adapter<WidgetAdapter.ViewHolder> {

    private List<MainActivity.WidgetApp> widgetList;

    public WidgetAdapter(List<MainActivity.WidgetApp> widgetList) {
        this.widgetList = widgetList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainActivity.WidgetApp app = widgetList.get(position);

        // Set the UI elements
        holder.nameText.setText(app.name);
        holder.packageText.setText(app.packageName);
        holder.iconImage.setImageDrawable(app.icon);

        // Click listener to open App Info / Settings
        holder.itemView.setOnClickListener(v -> {
            // Create intent for System App Settings
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

            // Point the intent specifically to this app's package
            Uri uri = Uri.fromParts("package", app.packageName, null);
            intent.setData(uri);

            // Start the settings activity
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return widgetList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView nameText, packageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.widgetIcon);
            nameText = itemView.findViewById(R.id.widgetName);
            packageText = itemView.findViewById(R.id.widgetPackage);
            //test
        }
    }
}