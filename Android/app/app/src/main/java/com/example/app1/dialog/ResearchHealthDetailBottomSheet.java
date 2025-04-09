package com.example.app1.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.models.ChatHistoryResponse;
import com.example.app1.models.ChatRequest;
import com.example.app1.models.ChatResponse;
import com.example.app1.models.Patient;
import com.example.app1.models.PatientHealthMetrics;
import com.example.app1.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResearchHealthDetailBottomSheet extends BottomSheetDialogFragment {
    private TextView textViewPatientName, textViewPatientPhone,
            textViewPatientGender, textViewPatientAge, textViewResearchDate, textViewResearchResult;
    private ImageView buttonClose ;
    private Button buttonAdice, buttonResearchAgain;
    private OnButtonClickListener listener;

    private Patient patient;
    private PatientHealthMetrics patientHealthMetrics;

    private ProgressBar progressBar;
    private AuthService apiService;
    private SessionManager sessionManager;

    public interface OnButtonClickListener {
        void onAdviceButtonClick();
        void onResearchAgainButtonClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResearchHealthDetailBottomSheet.OnButtonClickListener) {
            listener = (ResearchHealthDetailBottomSheet.OnButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DoctorSelectionListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout của BottomSheet
        View view = inflater.inflate(R.layout.bottom_sheet_research, container, false);
        apiService = ApiClient.getAuthService(getContext());
        sessionManager = new SessionManager(getContext());
        // Khởi tạo các view
        textViewPatientName = view.findViewById(R.id.textView_research_patient_name);
        textViewPatientPhone = view.findViewById(R.id.textView_research_patient_phone);
        textViewPatientGender = view.findViewById(R.id.textView_research_patient_gender);
        textViewPatientAge = view.findViewById(R.id.textView_research_patient_dob);
        textViewResearchDate = view.findViewById(R.id.textView_research_result_time);
        textViewResearchResult = view.findViewById(R.id.textView_research_result);
        progressBar = view.findViewById(R.id.progressBar_research);
        // Lấy dữ liệu từ arguments (được truyền từ Activity)

        patient = (Patient) getArguments().getSerializable("patient");
        patientHealthMetrics = (PatientHealthMetrics) getArguments().getSerializable("patient_health_metrics");
        if (patient != null) {
            textViewPatientName.setText(patient.getFull_name());
            textViewPatientPhone.setText(patient.getPhone());
            textViewPatientGender.setText(patient.getGender());
            textViewPatientAge.setText(String.valueOf(patient.getDate_of_birth()));
        }
        buttonClose = view.findViewById(R.id.btn_close_bottom_sheet_research);
        buttonAdice = view.findViewById(R.id.btn_cancel_research_advice);
        buttonResearchAgain = view.findViewById(R.id.btn_research_again);
        buttonResearchAgain.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dark_blue));
        buttonAdice.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dark_blue));

        buttonClose.setOnClickListener(v -> dismiss());
        buttonAdice.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdviceButtonClick();
            }
            dismiss();
        });
        buttonResearchAgain.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResearchAgainButtonClick();
            }
            dismiss();
        });

        researchHealth(patientHealthMetrics, patient);
        return view;
    }

    private void researchHealth(PatientHealthMetrics metrics, Patient patient_user) {
        StringBuilder message = new StringBuilder();

        message.append("! Bạn là một bác sĩ AI.*\n" +
                "Hãy phân tích tình trạng sức khỏe của bệnh nhân dựa trên các chỉ số sau, nếu chỉ số nào bằng 0 thì hãy **bỏ qua và không đề cập đến** (có thể chưa có dữ liệu). Nếu đã được hỏi trước đó, hãy phân tích lại dựa trên thông tin dưới đây.\n\n" +
                "Đối với mỗi chỉ số:\n" +
                "- Đánh giá giá trị là bình thường, cao hay thấp.\n" +
                "- Nếu bất thường, hãy đưa ra **lời khuyên ngắn gọn** và dễ hiểu.\n" +
                "- Nếu bình thường, chỉ cần ghi nhận điều đó.\n\n" +
                "*Cuối cùng, tổng kết sơ bộ tình trạng sức khỏe tổng thể và khuyến nghị nếu cần.*\n\n" +
                "📋 Thông tin bệnh nhân:\n" +
                "- Họ tên: " + patient_user.getFull_name() + "\n" +
                "- Giới tính: " + patient_user.getGender() + "\n" +
                "- Ngày sinh: " + patient_user.getDate_of_birth() + "\n\n" +
                "📈 Các chỉ số sức khỏe:\n");
        if (metrics.getSystolic_bp() != 0)
            message.append("* **Huyết áp tâm thu (Systolic BP):** ").append(metrics.getSystolic_bp()).append(" mmHg\n");
        if (metrics.getDiastolic_bp() != 0)
            message.append("* **Huyết áp tâm trương (Diastolic BP):** ").append(metrics.getDiastolic_bp()).append(" mmHg\n");
        if (metrics.getHeart_rate() != 0)
            message.append("* **Nhịp tim (Heart Rate):** ").append(metrics.getHeart_rate()).append(" bpm\n");
        if (metrics.getBody_temperature() != 0.0f)
            message.append("* **Nhiệt độ cơ thể (Body Temperature):** ").append(metrics.getBody_temperature()).append(" °C\n");
        if (metrics.getRespiratory_rate() != 0)
            message.append("* **Tần số hô hấp (Respiratory Rate):** ").append(metrics.getRespiratory_rate()).append(" lần/phút\n");
        if (metrics.getWeight_kg() != 0.0f)
            message.append("* **Cân nặng (Weight):** ").append(metrics.getWeight_kg()).append(" kg\n");
        if (metrics.getHeight_cm() != 0.0f)
            message.append("* **Chiều cao (Height):** ").append(metrics.getHeight_cm()).append(" cm\n");
        if (metrics.getBmi() != 0.0f)
            message.append("* **Chỉ số khối cơ thể (BMI):** ").append(metrics.getBmi()).append("\n");
        if (metrics.getBlood_glucose() != 0.0f)
            message.append("* **Đường huyết (Blood Glucose):** ").append(metrics.getBlood_glucose()).append(" mg/dL\n");
        if (metrics.getCholesterol_total() != 0.0f)
            message.append("* **Cholesterol toàn phần (Total Cholesterol):** ").append(metrics.getCholesterol_total()).append(" mg/dL\n");
        if (metrics.getLdl() != 0.0f)
            message.append("* **LDL Cholesterol (LDL):** ").append(metrics.getLdl()).append(" mg/dL\n");
        if (metrics.getHdl() != 0.0f)
            message.append("* **HDL Cholesterol (HDL):** ").append(metrics.getHdl()).append(" mg/dL\n");
        if (metrics.getTriglycerides() != 0.0f)
            message.append("* **Triglycerides:** ").append(metrics.getTriglycerides()).append(" mg/dL\n");
        if (metrics.getHemoglobin() != 0.0f)
            message.append("* **Hemoglobin:** ").append(metrics.getHemoglobin()).append(" g/dL\n");

        sendMessageToApi(message.toString());
    }


    private void sendMessageToApi(String message) {
        showLoading(true);
        String token = "Bearer " + sessionManager.getToken();
        ChatRequest request = new ChatRequest(message);

        apiService.sendChatMessage(token, request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse botResponse = response.body();

                    String formatted = botResponse.getReply()
                            .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>")         // **bold**
                            .replaceAll("\\*(.*?)\\*", "<i>$1</i>")               // *italic*
                            .replaceAll("\\*/(.*?)\\*/", "<u>$1</u>")             // */underline*/
                            .replaceAll("\\n", "<br>");                           // xuống dòng

                    Spanned spanned = Html.fromHtml(formatted, Html.FROM_HTML_MODE_LEGACY);
                    textViewResearchResult.setText(spanned);
                    textViewResearchDate.setText(botResponse.getTime_vn());
                } else {
                    textViewResearchDate.setText("Không thể lấy dữ liệu");
                    textViewResearchResult.setText("Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                showLoading(false);
                textViewResearchDate.setText("Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng và thử lại.");

            }
        });
    }
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
    @Override
    public int getTheme() {
        return R.style.BottomSheetTheme;
    }
}
