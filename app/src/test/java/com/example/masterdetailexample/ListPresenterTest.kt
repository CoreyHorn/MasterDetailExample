package com.example.masterdetailexample

import android.os.Parcelable
import com.example.masterdetailexample.list.ListPresenter
import com.example.masterdetailexample.list.models.ListAction
import com.example.masterdetailexample.list.models.ListEvent
import com.example.masterdetailexample.list.models.ListResult
import com.example.masterdetailexample.list.models.ListState
import com.example.masterdetailexample.room.Item
import com.example.masterdetailexample.room.ItemDao
import io.reactivex.Flowable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class ListPresenterTest {

    private val itemDao = mock(ItemDao::class.java)
    private val listState = mock(Parcelable::class.java)
    private val item = Item(0, "text", false)

    @Before
    fun setup() {
        //Force all schedulers to use the thread the tests are running on.
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        Mockito.`when`(itemDao.getItems()).thenReturn(Flowable.fromArray(emptyList()))
    }


    @Test
    fun testActionOutput() {

        val events = PublishSubject.create<ListEvent>()
        val actionObserver = TestObserver<ListAction>()

        val presenter = ListPresenter(itemDao)
        presenter.attachEventStream(events)
        presenter.actions().subscribe(actionObserver)

        actionObserver.assertNotComplete()
        actionObserver.assertNoErrors()
        actionObserver.assertValueCount(0)

        //Send all possible event types into events
        events.onNext(ListEvent.Create("whatever", listState))
        events.onNext(ListEvent.Delete(item, listState))
        events.onNext(ListEvent.Select(item, listState))
        events.onNext(ListEvent.ClearedInput(listState))
        events.onNext(ListEvent.UpdateListState(listState))

        //Ensure the output contains the appropriate types and values in the appropriate order
        actionObserver.assertValues(
                ListAction.Create("whatever", listState),
                ListAction.Delete(item, listState),
                ListAction.Select(item, listState),
                ListAction.ClearedInput(listState),
                ListAction.UpdateListState(listState)
        )
    }

    @Test
    fun testStateOutput() {

        val results = PublishSubject.create<ListResult>()
        val stateObserver = TestObserver<ListState>()

        val itemList = listOf(Item(0, "text", true))

        val presenter = ListPresenter(itemDao)
        presenter.attachResultStream(results)
        presenter.states().subscribe(stateObserver)

        stateObserver.assertNotComplete()
        stateObserver.assertNoErrors()

        //Send all possible result types into results
        results.onNext(ListResult.NewItems(itemList))
        results.onNext(ListResult.ClearedInput(listState))
        results.onNext(ListResult.Selected(listState))
        results.onNext(ListResult.UpdatedListState(listState))
        results.onNext(ListResult.RequestInProgress(listState))
        results.onNext(ListResult.NewItems(emptyList()))

        //We expect more states than results because states is a BehaviorSubject.
        stateObserver.assertValues(
                ListState(false, null, null),
                ListState(false, null, itemList),
                ListState(false, listState, itemList),
                ListState(false, listState, itemList),
                ListState(false, listState, itemList),
                ListState(true, listState, itemList),
                ListState(true, listState, emptyList())
        )
    }
}