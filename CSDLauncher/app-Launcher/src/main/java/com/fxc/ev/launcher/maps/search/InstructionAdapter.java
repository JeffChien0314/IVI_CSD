package com.fxc.ev.launcher.maps.search;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.DistanceConversions;
import com.fxc.ev.launcher.utils.view.NextInstructionImageHelper;
import com.tomtom.navkit2.guidance.Instruction;

import java.util.List;


public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.InstructionItemViewHolder> {
    public static final String TAG = "InstructionAdapter";
    private List<Instructions> mInstructionsList;
    private InstructionItemViewHolder instructionItemViewHolder;
    private LauncherActivity mLauncherActivity;
    private NextInstructionImageHelper nextInstructionImageHelper;


    public InstructionAdapter(LauncherActivity launcherActivity, List<Instructions> instructionList) {
        this.mLauncherActivity = launcherActivity;
        this.mInstructionsList = instructionList;
    }

    @NonNull
    @Override
    public InstructionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mLauncherActivity).inflate(R.layout.direction_list_item, parent, false);
        nextInstructionImageHelper = new NextInstructionImageHelper();
        instructionItemViewHolder = new InstructionItemViewHolder(view);
        return instructionItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionItemViewHolder holder, int position) {
        Instructions instruction = mInstructionsList.get(position);
        holder.initHolder(instruction);
    }


    @Override
    public int getItemCount() {
        return mInstructionsList.size();
    }

    class InstructionItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView directionIcon;
        private TextView direction;
        private TextView road;
        private TextView interval;


        public InstructionItemViewHolder(@NonNull View itemView) {
            super(itemView);
            directionIcon = itemView.findViewById(R.id.direction_icon);
            direction = itemView.findViewById(R.id.direction);
            road = itemView.findViewById(R.id.road);
            interval = itemView.findViewById(R.id.interval);
        }

        public void initHolder(Instructions instruction) {

            DistanceConversions.FormattedDistance fd = DistanceConversions.convert((int) (instruction.getDistance()), mLauncherActivity.getCurrentCountryCode());
            interval.setText(fd.distance + " " + fd.unit);
            direction.setText(instruction.getInstruction().getTurn().getDirection().name());
            road.setText(instruction.getInstruction().getNextSignificantRoad().getStreetName());
            nextInstructionImageHelper.setManeuverImageForInstruction(directionIcon, instruction.getInstruction());
        }
    }
}
