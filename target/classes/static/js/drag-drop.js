
/**
 * HTMLの読み込みが完了した後に中の処理を実行する
 * document: HTMLドキュメント全体を表すオブジェクト
 * addEventListener: イベントリスナーを追加するメソッド
 * 構文: 要素.addEventListener(イベント名, コールバック関数(アロー関数))
 * querySelectorAll: 指定されたCSSセレクタに一致する全ての要素を取得
 * '.task': クラス名が"task"の要素を指定
 */
document.addEventListener('DOMContentLoaded', () => {
	const tasks = document.querySelectorAll('.task');
	const columns = document.querySelectorAll('.column');
	
	/**
	 * ドラッグ開始
	 * forEach: 配列やNodeListの各要素に対して繰り返し処理を実行
	 * 
	 * setAttribute
	 * 用途: HTML要素に属性を設定
	 * 構文: 要素.setAttribute(属性名, 値)
	 * 結果: <div class="task" draggable="true">...</div>
	 */
	tasks.forEach(task => {
		task.setAttribute('draggable', true);//ドラッグを可能にする
		
		/**
		 * dragstartイベント
		 * 発生タイミング: ユーザーが要素をドラッグし始めた瞬間
		 * 引数: e（イベントオブジェクト）
		 * 
		 * dataTransfer
		 * 用途: ドラッグ&ドロップ操作でデータを転送
		 * 機能: ドラッグ元からドロップ先へ情報を渡す
		 * 
		 * setData
		 * 構文: dataTransfer.setData(形式, データ)
		 * 'text/plain': データの形式（プレーンテキスト）
		 * 
		 * task.dataset.taskId
		 * dataset: HTML要素のdata属性にアクセス
		 * 例: <div data-task-id="123"> → task.dataset.taskIdで"123"を取得
		 */
		task.addEventListener('dragstart', (e) => {
			e.dataTransfer.setData('text/plain', task.dataset.taskId);//タスクIdを渡す
			
		});
	});
	
	/**
	 * ドラッグ先の列に乗っている間の処理
	 * columns
	 * 前回取得した.columnクラスの全ての要素（NodeList）
	 * 
	 * forEach
	 * 各カラム要素に対して同じ処理を実行
	 * 
	 * dragoverイベント
	 * 発生タイミング: ドラッグ中の要素が対象要素の上にある間、継続的に発生
	 * 頻度: 非常に高頻度（マウスの動きに応じて）
	 * 用途: ドロップ可能な領域であることを示す
	 * 
	 * イベントの流れ
	 * dragstart - ドラッグ開始
	 * dragover - ドラッグ中の要素が上にある間（継続的）
	 * drop - ドロップ実行時
	 * 
	 * preventDefault
	 * 用途: ブラウザのデフォルトの動作を無効化
	 * 重要性: これがないとドロップが無効になる
	 */
		columns.forEach(column => {
			// Done列はドロップイベントを追加しない
    		if (column.id === 'Done') {
				return; // Done列はスキップ
    		}
    		
			column.addEventListener('dragover', (e) => {
				e.preventDefault();//ドロップを許可する
			});
			
			/**
				 * dropイベント
				 * 発生タイミング: ユーザーがドラッグ中の要素を実際にドロップした瞬間
				 * 条件: dragoverでpreventDefault()が呼ばれている必要がある
				 * 用途: ドロップ時の実際の処理を実行
				 * 
				 * dataTransfer.getData()
				 * 用途: ドラッグ開始時に保存されたデータを取得
				 * 引数: 'text/plain'（保存時と同じ形式を指定）
				 * 戻り値: ドラッグ開始時にsetData()で保存された値
				 * 
				 * column.id
				 * 用途: ドロップ先のカラムのID属性を取得
				 * 例: <div class="column" id="doing"> → newStatus = "doing"
				 */
			column.addEventListener('drop', (e) => {
				e.preventDefault();//ブラウザのデフォルト動作を防ぐ
				const taskId = e.dataTransfer.getData('text/plain'); // 渡されたタスクIDを取得
				const status = column.id;// 移動先の列（Next / Doing など）を新ステータスとする
				
				/**
				 * サーバーにステータス変更を送信
				 * fetch API
				 * 用途: サーバーとの通信を行う現代的なJavaScript API
				 * 特徴: Promiseベースで非同期処理
				 * 代替: 古いXMLHttpRequestの代わり
				 * 基本構文
				 * javascriptfetch(URL, オプション)
				 * 
				 * '/tasks/updateStatus'
				 * URL
				 * 相対パス: 現在のドメインからの相対パス
				 * 例: https://example.com/tasks/updateStatus
				 * 用途: サーバーのエンドポイント（処理を行う場所）
				 * 
				 * オプションオブジェクト
				 * headers
				 * Content-Type: 送信するデータの形式を指定
				 * application/x-www-form-urlencoded: HTMLフォームのデフォルト形式
				 * 
				 * body
				 * 送信データ: サーバーに送る実際のデータ
				 * テンプレートリテラル: バッククォートで囲んだ文字列
				 * 
				 * .then(() => { location.reload(); })
				 * Promise
				 * fetch()は非同期処理なのでPromiseを返す
				 * .then()で成功時の処理を定義
				 */
				fetch('/task/tasks/updateStatus', {
					method: 'POST',
					headers: {'Content-Type': 'application/x-www-form-urlencoded'},
					body: `taskId=${taskId}&status=${status}`
				})
				.then(response => {
					if (response.ok) {
						console.log('ステータス更新成功');
						location.reload();
        			} else {
						console.error('ステータス更新失敗');
        			}
    			})
    			.catch(error => {
					console.error('エラー:', error);
    			});
				
			});
				
		});
		
});
