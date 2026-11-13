package com.example.thi_giuaki_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context context;
    private List<Book> bookList;
    private OnBookListener listener;
    private boolean showActions;

    public interface OnBookListener {
        void onEdit(Book book);
        void onDelete(Book book);
    }

    public BookAdapter(Context context, List<Book> bookList, boolean showActions, OnBookListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.showActions = showActions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvMaSach.setText("Mã sách: " + book.getMaSach());
        holder.tvTenSach.setText("Tên sách: " + book.getTenSach());
        holder.tvTacGia.setText("Tác giả: " + book.getTacGia());
        holder.tvTheLoai.setText("Thể loại: " + book.getTheLoai());
        
        if (book.getTrangThai() == 0) {
            holder.tvTrangThai.setText("Trạng thái: Có sẵn");
            holder.tvTrangThai.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvTrangThai.setText("Trạng thái: Đang được mượn");
            holder.tvTrangThai.setTextColor(Color.parseColor("#C62828"));
        }

        if (showActions && listener != null) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEdit(book));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(book));
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaSach, tvTenSach, tvTacGia, tvTheLoai, tvTrangThai;
        Button btnEdit, btnDelete;
        LinearLayout layoutActions;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaSach = itemView.findViewById(R.id.tvMaSach);
            tvTenSach = itemView.findViewById(R.id.tvTenSach);
            tvTacGia = itemView.findViewById(R.id.tvTacGia);
            tvTheLoai = itemView.findViewById(R.id.tvTheLoai);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutActions = itemView.findViewById(R.id.layoutBookActions);
        }
    }
}
