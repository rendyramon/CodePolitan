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

package id.zelory.codepolitan.controller;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import id.zelory.benih.controller.BenihController;
import id.zelory.benih.util.BenihScheduler;
import id.zelory.benih.util.BenihWorker;
import id.zelory.codepolitan.controller.event.ErrorEvent;
import id.zelory.codepolitan.data.model.Tag;
import id.zelory.codepolitan.data.api.CodePolitanApi;
import id.zelory.codepolitan.data.database.DataBaseHelper;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : August 6, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TagController extends BenihController<TagController.Presenter>
{
    private List<Tag> tags;
    private List<Tag> popularTags;

    public TagController(Presenter presenter)
    {
        super(presenter);
    }

    public void loadTags(int page)
    {
        presenter.showLoading();
        CodePolitanApi.pluck()
                .getApi()
                .getTags(page)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .flatMap(articleListResponse -> Observable.from(articleListResponse.getResult()))
                .map(tag -> {
                    tag.setFollowed(DataBaseHelper.pluck().isFollowed(tag));
                    return tag;
                })
                .toList()
                .subscribe(tags -> {
                    BenihWorker.pluck()
                            .doInNewThread(() -> {
                                if (page == 1)
                                {
                                    this.tags = tags;
                                } else
                                {
                                    this.tags.addAll(tags);
                                }
                            })
                            .subscribe(o -> {
                                if (presenter != null)
                                {
                                    presenter.showPopularTags(tags);
                                }
                            });
                    if (presenter != null)
                    {
                        presenter.dismissLoading();
                    }
                }, throwable -> {
                    if (presenter != null)
                    {
                        Timber.d(throwable.getMessage());
                        presenter.showError(new Throwable(ErrorEvent.LOAD_LIST_TAG));
                        presenter.dismissLoading();
                    }
                });
    }

    public void loadPopularTags(int page)
    {
        presenter.showLoading();
        CodePolitanApi.pluck()
                .getApi()
                .getPopularTags(page)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .flatMap(articleListResponse -> Observable.from(articleListResponse.getResult()))
                .map(tag -> {
                    tag.setFollowed(DataBaseHelper.pluck().isFollowed(tag));
                    return tag;
                })
                .toList()
                .subscribe(tags -> {
                    BenihWorker.pluck()
                            .doInNewThread(() -> {
                                if (page == 1)
                                {
                                    popularTags = tags;
                                } else
                                {
                                    popularTags.addAll(tags);
                                }
                            })
                            .subscribe(o -> {
                                if (presenter != null)
                                {
                                    presenter.showPopularTags(tags);
                                }
                            });
                    if (presenter != null)
                    {
                        presenter.dismissLoading();
                    }
                }, throwable -> {
                    if (presenter != null)
                    {
                        Timber.d(throwable.getMessage());
                        presenter.showError(new Throwable(ErrorEvent.LOAD_POPULAR_TAGS));
                        presenter.dismissLoading();
                    }
                });
    }

    public void filter(String query)
    {
        if (popularTags != null)
        {
            Observable.from(popularTags)
                    .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                    .filter(tag -> tag.getName().toLowerCase().contains(query.toLowerCase()))
                    .map(tag -> {
                        tag.setFollowed(DataBaseHelper.pluck().isFollowed(tag));
                        return tag;
                    })
                    .toList()
                    .subscribe(presenter::showFilteredTag, presenter::showError);
        }
    }

    @Override
    public void saveState(Bundle bundle)
    {
        bundle.putParcelableArrayList("tags", (ArrayList<Tag>) tags);
        bundle.putParcelableArrayList("popular_tags", (ArrayList<Tag>) popularTags);
    }

    @Override
    public void loadState(Bundle bundle)
    {
        tags = bundle.getParcelableArrayList("tags");
        if (tags != null)
        {
            presenter.showTags(tags);
        } else
        {
            presenter.showError(new Throwable(ErrorEvent.LOAD_STATE_LIST_TAG));
        }

        popularTags = bundle.getParcelableArrayList("popular_tags");
        if (popularTags != null)
        {
            presenter.showPopularTags(popularTags);
        } else
        {
            presenter.showError(new Throwable(ErrorEvent.LOAD_STATE_POPULAR_TAGS));
        }
    }

    public interface Presenter extends BenihController.Presenter
    {
        void showTags(List<Tag> tags);

        void showPopularTags(List<Tag> popularTags);

        void showFilteredTag(List<Tag> tags);
    }
}
