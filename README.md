# Kibaan

基本的な使用方法については`iOS-Kibaan`と同じな為、`iOS-Kibaan`の[README](https://github.com/altonotes/iOS-Kibaan/blob/master/README.md)を参照。
本READMEには、Android版特有のことについてのみ説明する。  

# バージョンアップ方法

1. kibaan/build.gradle に記載の`kibaanVersion`の数字を更新する。
2. プロジェクト直下にある`./buildAndArchive.sh`を実行する
3. GitHubリポジトリにPUSHする

## 開発環境
* Android Studio(v3.2)

## 内包ライブラリ（サードパーティ）
* ButterKnife
* OKHttp
* Gson

## Download
以下を`build.gradle`に記述することでダウンロード可能。

```
repositories {
    maven { url 'http://altonotes.github.io/Android-Kibaan/repository' }
}
```

```
dependencies {
	implementation 'jp.co.altonotes.kibaan:kibaan:0.6.0'
}
```

## ガイドライン

### ViewControllerに対応するXML
画面(ViewController)に対応するXMLファイルは、ViewControllerのクラス名称をスネークケースで変換した名称とすること。  

***命名例***

```
SampleViewController.kt
↓
sample_view_controller.xml
```

### TableViewCellに対応するXML
`UITableView`のセル(TableViewCell)に対応するXMLファイルは、TableViewCellのクラス名称をスネークケースで変換した名称とすること。  

***命名例***

```
SampleTableViewCell.kt
↓
sample_table_view_cell.xml
```

### メッセージの管理
アプリで表示するメッセージは`strings.xml`に記述し、以下のような記述で参照する。

***strings.xml***  

```
<string name="msg_0001">ログインの有効期限が切れました。</string>
```  
***使用方法***

```
"msg_0001".localizedString
```

### ログ出力(Log)
Android標準の`Log`クラスをラップし、指定したレベルまでのログ出力の抑制が可能なクラス。  
※Release時はログ出力を抑制する為などに使用する
Android標準の`Log`クラスは使用しないこと。

***使用例***
```
Log.d("tag", "message.")
```

***ログ出力レベルの指定方法***
```
Log.level = Log.Level.error
```
