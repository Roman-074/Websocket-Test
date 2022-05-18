package websocket.test.inapp.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import websocket.test.inapp.*
import websocket.test.lib.ISocketProvider

class MainViewModel(
    val iSocket: ISocketProvider<TickerData>,
) : ViewModel() {

    // Одноразовый контейнер, который может вмещать несколько других одноразовых айтемов
    // и предлагает сложность добавления и удаления O (1)
    private val compositeDisposable = CompositeDisposable()

    private val _ticker = MutableLiveData<Ticker>()
    val ticker: LiveData<Ticker>
        get() = _ticker

    init {
        compositeDisposable.add(
            iSocket.observeTicker(createTickerRequest())
                .observeOn(AndroidSchedulers.mainThread())
                .map { tickerData -> tickerData.toTickerModel() }
                .subscribe({ ticker ->
                    _ticker.postValue(ticker)
                }, { e ->
                    Timber.e("observeTickerUseCase error: %s", e.toString())
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}
