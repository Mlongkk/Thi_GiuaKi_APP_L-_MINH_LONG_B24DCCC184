package com.example.thi_giuaki_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BorrowSlipAdapter extends RecyclerView.Adapter<BorrowSlipAdapter.BorrowViewHolder> {
    private Context context;
    private List<BorrowSlip> slipList;
    private OnReturnListener listener;

    public interface OnReturnListener {
        void onReturnBook(BorrowSlip slip);
    }

    public BorrowSlipAdapter(Context context, List<BorrowSlip> slipList, OnReturnListener listener) {
        this.context = context;
        this.slipList = slipList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BorrowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrow_slip, parent, false);
        return new BorrowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowViewHolder holder, int position) {
        BorrowSlip slip = slipList.get(position);
        holder.tvMaSach.setText("Mã phiếu #" + slip.getId() + " - Mã sách: " + slip.getMaSach());
        holder.tvNguoiMuon.setText("Người mượn: " + slip.getTenNguoiMuon() + " - SĐT: " + slip.getSdt());
        holder.tvNgay.setText("Ngày mượn: " + slip.getNgayMuon() + " | Dự kiến trả: " + slip.getNgayTra());
        holder.tvTrangThai.setText("Trạng thái: " + slip.getTrangThaiPhieu());

        if ("Đã trả".equals(slip.getTrangThaiPhieu())) {
            holder.tvTrangThai.setTextColor(Color.parseColor("#2E7D32"));
            holder.btnReturn.setVisibility(View.GONE);
        } else {
            holder.tvTrangThai.setTextColor(Color.parseColor("#E65100"));
            holder.btnReturn.setVisibility(View.VISIBLE);
            holder.btnReturn.setOnClickListener(v -> listener.onReturnBook(slip));
        }
    }

    @Override
    public int getItemCount() {
        return slipList.size();
    }

    public static class BorrowViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaSach, tvNguoiMuon, tvNgay, tvTrangThai;
        Button btnReturn;

        public BorrowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaSach = itemView.findViewById(R.id.tvBorrowMaSach);
            tvNguoiMuon = itemView.findViewById(R.id.tvBorrowNguoiMuon);
            tvNgay = itemView.findViewById(R.id.tvBorrowNgay);
            tvTrangThai = itemView.findViewById(R.id.tvBorrowTrangThai);
            btnReturn = itemView.findViewById(R.id.btnReturnBook);
        }
    }
}
