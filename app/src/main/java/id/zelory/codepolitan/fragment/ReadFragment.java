/*
 * Copyright (c) 2015 Zelory.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package id.zelory.codepolitan.fragment;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import id.zelory.benih.fragment.BenihFragment;
import id.zelory.benih.view.BenihImageView;
import id.zelory.codepolitan.R;
import id.zelory.codepolitan.controller.ArticleController;
import id.zelory.codepolitan.model.Article;
import timber.log.Timber;

/**
 * Created by zetbaitsu on 7/28/15.
 */
public class ReadFragment extends BenihFragment<Article> implements ArticleController.Presenter
{
    private ArticleController articleController;
    @Bind(R.id.image) BenihImageView image;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.title) TextView title;
    //@Bind(R.id.content) WebView content;

    @Override
    protected int getFragmentView()
    {
        return R.layout.fragment_read;
    }

    @Override
    protected void onViewReady(Bundle bundle)
    {
        /*content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        content.setVerticalScrollBarEnabled(false);
        content.setHorizontalScrollBarEnabled(false);
        content.setWebChromeClient(new WebChromeClient());*/

        setupController(bundle);
    }

    private void setupController(Bundle bundle)
    {
        if (articleController == null)
        {
            articleController = new ArticleController(this);
        }

        if (bundle != null)
        {
            articleController.loadState(bundle);
        } else
        {
            articleController.loadArticle(data.getId());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        articleController.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showArticle(Article article)
    {
        Timber.d(article.getThumbnail());
        image.setImageUrl(article.getThumbnail());
        date.setText(article.getDate());
        title.setText(article.getTitle());
        //content.loadData(article.getContent(), "text/html", "UTF-8");
    }

    @Override
    public void showArticles(List<Article> articles)
    {

    }

    @Override
    public void showFilteredArticles(List<Article> articles)
    {

    }

    @Override
    public void showError(Throwable throwable)
    {

    }
}
