# はじめに

基本的な使用方法については`iOS-Kibaan`と同じな為、`iOS-Kibaan`の[README](https://github.com/altonotes/iOS-Kibaan/blob/master/README.md)を参照。
本READMEには、Android版特有のことについてのみ説明する。  

## 開発環境
* Android Studio(v3.4)

# リリース方法

1. kibaan/build.gradle に記載の`kibaanVersion`の数字を更新する。
2. プロジェクト直下にある`./buildAndArchive.sh`を実行する
3. GitHubリポジトリにPUSHする

## 内包ライブラリ（サードパーティ）
* ButterKnife
* OKHttp
* Gson

## アプリへの組み込み方法
以下を`build.gradle`に記述することでダウンロード可能。

```
repositories {
    maven { url 'http://altonotes.github.io/Android-Kibaan/repository' }
}
```

```
dependencies {
	implementation 'jp.co.altonotes.kibaan:kibaan:0.7.76' // バージョンは最新を確認する
}
```

## デバッグ方法

Kibaanをアプリに組み込んだ状態でKibaan内のクラスのデバッグを行いたい場合は以下の設定を行う。

- 本プロジェクトをGitHubからCloneし、アプリのプロジェクトと同じディレクトリに配置する
- settings.gradle の内容をいかに変更

```
include ':app', ':kibaan'
project(':kibaan').projectDir = new File('../Android-Kibaan/kibaan')
```

- build.gradle のKibaanのdependencyを以下に変更

```
implementation project(':kibaan')
implementation 'com.google.code.gson:gson:2.8.2'
```

## ガイドライン

### レイアウトXMLファイルの命名規則

- ViewControllerに対応するXMLファイルは、ViewControllerのクラス名をスネークケースに変えた名前にする
- UITableViewCellに対応するXMLファイルは、TableViewCellのクラス名をスネークケースに変えた名前にする

ただし、一つのViewControllerに対して複数のレイアウトファイルを使いたい場合、任意のレイアウトファイル名をつけてViewControllerの引数にファイル名を渡すことができる。

***命名例***

```
SampleViewController.kt → sample_view_controller.xml
HTMLViewController.kt → html_view_controller.xml
SampleTableViewCell.kt → sample_table_view_cell.xml
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
