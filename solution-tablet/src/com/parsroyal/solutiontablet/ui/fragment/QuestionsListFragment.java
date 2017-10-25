package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.QuestionsAdapter;
import java.util.List;


public class QuestionsListFragment extends BaseFragment {

  @BindView(R.id.title_tv)
  TextView titleTv;
  @BindView(R.id.questionnaire_size_tv)
  TextView questionnaireSizeTv;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  Unbinder unbinder;

  private MainActivity mainActivity;
  private QuestionnaireServiceImpl questionnaireService;
  private CustomerServiceImpl customerService;
  private GoodsServiceImpl goodsService;
  private VisitServiceImpl visitService;
  private long questionnaireBackendId;
  private QuestionnaireListModel questionnaire;
  private long visitId;
  private long customerId;
  private long goodsGroupBackendId;
  private int parent;
  private long answersGroupNo;
  private Customer customer;

  public QuestionsListFragment() {
    // Required empty public constructor
  }


  public static QuestionsListFragment newInstance() {
    return new QuestionsListFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_questions_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    customerService = new CustomerServiceImpl(mainActivity);
    goodsService = new GoodsServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    mainActivity.changeTitle(getString(R.string.questionnaire_page));

    Bundle arguments = getArguments();
    questionnaireBackendId = arguments.getLong(Constants.QUESTIONNAIRE_BACKEND_ID);
    questionnaire = (QuestionnaireListModel) arguments.getSerializable(Constants.QUESTIONNAIRE_OBJ);
    visitId = arguments.getLong(Constants.VISIT_ID);
    customerId = arguments.getLong(Constants.CUSTOMER_ID);
    goodsGroupBackendId = arguments.getLong(Constants.GOODS_GROUP_BACKEND_ID);
    parent = arguments.getInt(Constants.PARENT, 0);
    answersGroupNo = arguments.getLong(Constants.ANSWERS_GROUP_NO, -1);

    customer = customerService.getCustomerById(customerId);

    setUpRecyclerView();

    questionnaireSizeTv.setText(String.valueOf(questionnaire.getQuestionsCount()));
    titleTv.setText(questionnaire.getDescription());
    return view;
  }

  private void setUpRecyclerView() {
    QuestionsAdapter questionsAdapter = new QuestionsAdapter(mainActivity, getQuestions(), visitId,
        goodsGroupBackendId, answersGroupNo, customerId, questionnaireBackendId);
    LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(questionsAdapter);
  }

  private List<QuestionListModel> getQuestions() {
    QuestionSo questionSo = new QuestionSo();
    questionSo.setQuestionnaireBackendId(questionnaireBackendId);
    questionSo.setVisitId(visitId);
    questionSo.setGoodsBackendId(goodsGroupBackendId == -1 ? null : goodsGroupBackendId);
    questionSo.setAnswersGroupNo(answersGroupNo);
    List<QuestionListModel> questionListModels = questionnaireService
        .searchForQuestions(questionSo);
    questionnaire.setQuestionsCount(questionListModels.size());
    return questionListModels;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.QUESTION_LIST_FRAGMENT_ID;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}