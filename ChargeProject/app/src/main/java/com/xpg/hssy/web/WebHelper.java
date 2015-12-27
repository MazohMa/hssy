package com.xpg.hssy.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.protocol.HTTP;

/**
 * HttpHeader
 * 
 * @Description
 * @author Joke Huang
 * @createDate 2014年6月27日
 * @version 1.0.0
 */

public class WebHelper {
	// 1、HTTP请求方式
	// 说明：
	// 主要使用到“GET”和“POST”。
	//
	// 实例：
	// POST /test/tupian/cm HTTP/1.1
	//
	// 分成三部分：
	// （1）POST：HTTP请求方式
	// （2）/test/tupian/cm：请求Web服务器的目录地址（或者指令）
	// （3）HTTP/1.1: URI（Uniform Resource Identifier，统一资源标识符）及其版本
	//
	// 备注：
	// 在Ajax中，对应method属性设置。
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	// 2、Host
	// 说明：
	// 请求的web服务器域名地址
	//
	// 实例：
	// 例如web请求URL：http://zjm-forum-test10.zjm.baidu.com:8088/test/tupian/cm
	// Host就为zjm-forum-test10.zjm.baidu.com:8088
	public static final String KEY_HOST = "Host:";

	// 3、User-Agent
	// 说明：
	// HTTP客户端运行的浏览器类型的详细信息。通过该头部信息，web服务器可以判断到当前HTTP请求的客户端浏览器类别。
	//
	// 实例：
	// User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11)
	// Gecko/20071127 Firefox/2.0.0.11
	public static final String KEY_USER_AGENT = "User-Agent:";

	// 4、Accept
	// 说明：
	// 指定客户端能够接收的内容类型，内容类型中的先后次序表示客户端接收的先后次序。
	//
	// 实例：
	// Accept:text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
	//
	// 备注：
	// 在Prototyp（1.5）的Ajax代码封装中，将Accept默认设置为“text/javascript, text/html,
	// application/xml, text/xml, */*”。这是因为Ajax默认获取服务器返回的Json数据模式。
	//
	// 在Ajax代码中，可以使用XMLHttpRequest 对象中setRequestHeader函数方法来动态设置这些Header信息。
	public static final String KEY_ACCEPT = "Accept:";

	// 5、Accept-Language
	// 说明：
	// 指定HTTP客户端浏览器用来展示返回信息所优先选择的语言。
	//
	// 实例：
	// Accept-Language: zh-cn,zh;q=0.5
	// 这里默认为中文。
	public static final String KEY_ACCEPT_LANGUAGE = "Accept-Language:";

	// 6、Accept-Encoding
	// 说明：
	// 指定客户端浏览器可以支持的web服务器返回内容压缩编码类型。表示允许服务器在将输出内容发送到客户端以前进行压缩，以节约带宽。而这里设置的就是客户端浏览器所能够支持的返回压缩格式。
	//
	// 实例：
	// Accept-Encoding: gzip,deflate
	//
	// 备注：
	// 其实在百度很多产品线中，apache在给客户端返回页面数据之前，将数据以gzip格式进行压缩。
	// 另外有关deflate压缩介绍：
	// http://man.chinaunix.net/newsoft/ApacheMenual_CN_2.2new/mod/mod_deflate.html
	public static final String KEY_ACCEPT_ENCODING = "Accept-Encoding:";

	// 7、Accept-Charset
	// 说明：
	// 浏览器可以接受的字符编码集。
	//
	// 实例：
	// Accept-Charset: gb2312,utf-8;q=0.7,*;q=0.7
	public static final String KEY_ACCEPT_CHARSET = "Accept-Charset:";

	// 8、Content-Type
	// 说明：
	// 显示此HTTP请求提交的内容类型。一般只有post提交时才需要设置该属性。
	//
	// 实例：
	// Content-type: application/x-www-form-urlencoded;charset:UTF-8
	// 有关Content-Type属性值可以如下两种编码类型：
	// （1）“application/x-www-form-urlencoded”：
	// 表单数据向服务器提交时所采用的编码类型，默认的缺省值就是“application/x-www-form-urlencoded”。
	// 然而，在向服务器发送大量的文本、包含非ASCII字符的文本或二进制数据时这种编码方式效率很低。
	// （2）“multipart/form-data”：
	// 在文件上载时，所使用的编码类型应当是“multipart/form-data”，它既可以发送文本数据，也支持二进制数据上载。
	// 当提交为单单数据时，可以使用“application/x-www-form-urlencoded”；当提交的是文件时，就需要使用“multipart/form-data”编码类型。
	// 在Content-Type属性当中还是指定提交内容的charset字符编码。一般不进行设置，它只是告诉web服务器post提交的数据采用的何种字符编码。
	// 一般在开发过程，是由前端工程与后端UI工程师商量好使用什么字符编码格式来post提交的，然后后端ui工程师按照固定的字符编码来解析提交的数据。所以这里设置的charset没有多大作用。
	public static final String KEY_CONTENT_TYPE = "Content-Type:";
	public static final String VALUE_CONTENT_TYPE_APPLICATION = "application/x-www-form-urlencoded";
	public static final String VALUE_CONTENT_TYPE_STREAM = "application/octet-stream";
	public static final String VALUE_CONTENT_TYPE_JSON = "application/json";
	public static final String VALUE_CONTENT_TYPE_MULTIPART = "multipart/form-data";

	// 9、Connection
	// 说明：
	// 表示是否需要持久连接。如果web服务器端看到这里的值为“Keep-Alive”，或者看到请求使用的是HTTP 1.1（HTTP
	// 1.1默认进行持久连接），它就可以利用持久连接的优点，当页面包含多个元素时（例如Applet，图片），显著地减少下载所需要的时间。要实现这一点，
	// web服务器需要在返回给客户端HTTP头信息中发送一个Content-Length（返回信息正文的长度）头，最简单的实现方法是：先把内容写入ByteArrayOutputStream，然
	// 后在正式写出内容之前计算它的大小。
	//
	// 实例：
	// Connection: keep-alive
	public static final String KEY_CONNECTION = "Connection:";

	// 10、Keep-Alive
	// 说明：
	// 显示此HTTP连接的Keep-Alive时间。使客户端到服务器端的连接持续有效，当出现对服务器的后继请求时，Keep-Alive功能避免了建立或者重新建立连接。
	// 以前HTTP请求是一站式连接，从HTTP/1.1协议之后，就有了长连接，即在规定的Keep-Alive时间内，连接是不会断开的。
	//
	// 实例：
	// Keep-Alive: 300
	public static final String KEY_KEEP_ALIVE = "Keep-Alive:";

	// 11、cookie
	// 说明：
	// HTTP请求发送时，会把保存在该请求域名下的所有cookie值一起发送给web服务器。
	public static final String KEY_COOKIE = "cookie:";

	// 12、Referer
	// 说明：
	// 包含一个URL，用户从该URL代表的页面出发访问当前请求的页面
	public static final String KEY_REFERER = "Referer:";

	// TODO
	// "文件后缀名" = "ContentType类型"
	//
	// ".*"="application/octet-stream"
	// ".001"="application/x-001"
	// ".301"="application/x-301"
	// ".323"="text/h323"
	// ".906"="application/x-906"
	// ".907"="drawing/907"
	// ".a11"="application/x-a11"
	// ".acp"="audio/x-mei-aac"
	// ".ai"="application/postscript"
	// ".aif"="audio/aiff"
	// ".aifc"="audio/aiff"
	// ".aiff"="audio/aiff"
	// ".anv"="application/x-anv"
	// ".asa"="text/asa"
	// ".asf"="video/x-ms-asf"
	// ".asp"="text/asp"
	// ".asx"="video/x-ms-asf"
	// ".au"="audio/basic"
	// ".avi"="video/avi"
	// ".awf"="application/vnd.adobe.workflow"
	// ".biz"="text/xml"
	// ".bmp"="application/x-bmp"
	// ".bot"="application/x-bot"
	// ".c4t"="application/x-c4t"
	// ".c90"="application/x-c90"
	// ".cal"="application/x-cals"
	// ".cat"="application/vnd.ms-pki.seccat"
	// ".cdf"="application/x-netcdf"
	// ".cdr"="application/x-cdr"
	// ".cel"="application/x-cel"
	// ".cer"="application/x-x509-ca-cert"
	// ".cg4"="application/x-g4"
	// ".cgm"="application/x-cgm"
	// ".cit"="application/x-cit"
	// ".class"="java/*"
	// ".cml"="text/xml"
	// ".cmp"="application/x-cmp"
	// ".cmx"="application/x-cmx"
	// ".cot"="application/x-cot"
	// ".crl"="application/pkix-crl"
	// ".crt"="application/x-x509-ca-cert"
	// ".csi"="application/x-csi"
	// ".css"="text/css"
	// ".cut"="application/x-cut"
	// ".dbf"="application/x-dbf"
	// ".dbm"="application/x-dbm"
	// ".dbx"="application/x-dbx"
	// ".dcd"="text/xml"
	// ".dcx"="application/x-dcx"
	// ".der"="application/x-x509-ca-cert"
	// ".dgn"="application/x-dgn"
	// ".dib"="application/x-dib"
	// ".dll"="application/x-msdownload"
	// ".doc"="application/msword"
	// ".dot"="application/msword"
	// ".drw"="application/x-drw"
	// ".dtd"="text/xml"
	// ".dwf"="Model/vnd.dwf"
	// ".dwf"="application/x-dwf"
	// ".dwg"="application/x-dwg"
	// ".dxb"="application/x-dxb"
	// ".dxf"="application/x-dxf"
	// ".edn"="application/vnd.adobe.edn"
	// ".emf"="application/x-emf"
	// ".eml"="message/rfc822"
	// ".ent"="text/xml"
	// ".epi"="application/x-epi"
	// ".eps"="application/x-ps"
	// ".eps"="application/postscript"
	// ".etd"="application/x-ebx"
	// ".exe"="application/x-msdownload"
	// ".fax"="image/fax"
	// ".fdf"="application/vnd.fdf"
	// ".fif"="application/fractals"
	// ".fo"="text/xml"
	// ".frm"="application/x-frm"
	// ".g4"="application/x-g4"
	// ".gbr"="application/x-gbr"
	// ".gcd"="application/x-gcd"
	// ".gif"="image/gif"
	// ".gl2"="application/x-gl2"
	// ".gp4"="application/x-gp4"
	// ".hgl"="application/x-hgl"
	// ".hmr"="application/x-hmr"
	// ".hpg"="application/x-hpgl"
	// ".hpl"="application/x-hpl"
	// ".hqx"="application/mac-binhex40"
	// ".hrf"="application/x-hrf"
	// ".hta"="application/hta"
	// ".htc"="text/x-component"
	// ".htm"="text/html"
	// ".html"="text/html"
	// ".htt"="text/webviewhtml"
	// ".htx"="text/html"
	// ".icb"="application/x-icb"
	// ".ico"="image/x-icon"
	// ".ico"="application/x-ico"
	// ".iff"="application/x-iff"
	// ".ig4"="application/x-g4"
	// ".igs"="application/x-igs"
	// ".iii"="application/x-iphone"
	// ".img"="application/x-img"
	// ".ins"="application/x-internet-signup"
	// ".isp"="application/x-internet-signup"
	// ".IVF"="video/x-ivf"
	// ".java"="java/*"
	// ".jfif"="image/jpeg"
	// ".jpe"="image/jpeg"
	// ".jpe"="application/x-jpe"
	// ".jpeg"="image/jpeg"
	// ".jpg"="image/jpeg"
	// ".jpg"="application/x-jpg"
	// ".js"="application/x-javascript"
	// ".jsp"="text/html"
	// ".la1"="audio/x-liquid-file"
	// ".lar"="application/x-laplayer-reg"
	// ".latex"="application/x-latex"
	// ".lavs"="audio/x-liquid-secure"
	// ".lbm"="application/x-lbm"
	// ".lmsff"="audio/x-la-lms"
	// ".ls"="application/x-javascript"
	// ".ltr"="application/x-ltr"
	// ".m1v"="video/x-mpeg"
	// ".m2v"="video/x-mpeg"
	// ".m3u"="audio/mpegurl"
	// ".m4e"="video/mpeg4"
	// ".mac"="application/x-mac"
	// ".man"="application/x-troff-man"
	// ".math"="text/xml"
	// ".mdb"="application/msaccess"
	// ".mdb"="application/x-mdb"
	// ".mfp"="application/x-shockwave-flash"
	// ".mht"="message/rfc822"
	// ".mhtml"="message/rfc822"
	// ".mi"="application/x-mi"
	// ".mid"="audio/mid"
	// ".midi"="audio/mid"
	// ".mil"="application/x-mil"
	// ".mml"="text/xml"
	// ".mnd"="audio/x-musicnet-download"
	// ".mns"="audio/x-musicnet-stream"
	// ".mocha"="application/x-javascript"
	// ".movie"="video/x-sgi-movie"
	// ".mp1"="audio/mp1"
	// ".mp2"="audio/mp2"
	// ".mp2v"="video/mpeg"
	// ".mp3"="audio/mp3"
	// ".mp4"="video/mpeg4"
	// ".mpa"="video/x-mpg"
	// ".mpd"="application/vnd.ms-project"
	// ".mpe"="video/x-mpeg"
	// ".mpeg"="video/mpg"
	// ".mpg"="video/mpg"
	// ".mpga"="audio/rn-mpeg"
	// ".mpp"="application/vnd.ms-project"
	// ".mps"="video/x-mpeg"
	// ".mpt"="application/vnd.ms-project"
	// ".mpv"="video/mpg"
	// ".mpv2"="video/mpeg"
	// ".mpw"="application/vnd.ms-project"
	// ".mpx"="application/vnd.ms-project"
	// ".mtx"="text/xml"
	// ".mxp"="application/x-mmxp"
	// ".net"="image/pnetvue"
	// ".nrf"="application/x-nrf"
	// ".nws"="message/rfc822"
	// ".odc"="text/x-ms-odc"
	// ".out"="application/x-out"
	// ".p10"="application/pkcs10"
	// ".p12"="application/x-pkcs12"
	// ".p7b"="application/x-pkcs7-certificates"
	// ".p7c"="application/pkcs7-mime"
	// ".p7m"="application/pkcs7-mime"
	// ".p7r"="application/x-pkcs7-certreqresp"
	// ".p7s"="application/pkcs7-signature"
	// ".pc5"="application/x-pc5"
	// ".pci"="application/x-pci"
	// ".pcl"="application/x-pcl"
	// ".pcx"="application/x-pcx"
	// ".pdf"="application/pdf"
	// ".pdf"="application/pdf"
	// ".pdx"="application/vnd.adobe.pdx"
	// ".pfx"="application/x-pkcs12"
	// ".pgl"="application/x-pgl"
	// ".pic"="application/x-pic"
	// ".pko"="application/vnd.ms-pki.pko"
	// ".pl"="application/x-perl"
	// ".plg"="text/html"
	// ".pls"="audio/scpls"
	// ".plt"="application/x-plt"
	// ".png"="image/png"
	// ".png"="application/x-png"
	// ".pot"="application/vnd.ms-powerpoint"
	// ".ppa"="application/vnd.ms-powerpoint"
	// ".ppm"="application/x-ppm"
	// ".pps"="application/vnd.ms-powerpoint"
	// ".ppt"="application/vnd.ms-powerpoint"
	// ".ppt"="application/x-ppt"
	// ".pr"="application/x-pr"
	// ".prf"="application/pics-rules"
	// ".prn"="application/x-prn"
	// ".prt"="application/x-prt"
	// ".ps"="application/x-ps"
	// ".ps"="application/postscript"
	// ".ptn"="application/x-ptn"
	// ".pwz"="application/vnd.ms-powerpoint"
	// ".r3t"="text/vnd.rn-realtext3d"
	// ".ra"="audio/vnd.rn-realaudio"
	// ".ram"="audio/x-pn-realaudio"
	// ".ras"="application/x-ras"
	// ".rat"="application/rat-file"
	// ".rdf"="text/xml"
	// ".rec"="application/vnd.rn-recording"
	// ".red"="application/x-red"
	// ".rgb"="application/x-rgb"
	// ".rjs"="application/vnd.rn-realsystem-rjs"
	// ".rjt"="application/vnd.rn-realsystem-rjt"
	// ".rlc"="application/x-rlc"
	// ".rle"="application/x-rle"
	// ".rm"="application/vnd.rn-realmedia"
	// ".rmf"="application/vnd.adobe.rmf"
	// ".rmi"="audio/mid"
	// ".rmj"="application/vnd.rn-realsystem-rmj"
	// ".rmm"="audio/x-pn-realaudio"
	// ".rmp"="application/vnd.rn-rn_music_package"
	// ".rms"="application/vnd.rn-realmedia-secure"
	// ".rmvb"="application/vnd.rn-realmedia-vbr"
	// ".rmx"="application/vnd.rn-realsystem-rmx"
	// ".rnx"="application/vnd.rn-realplayer"
	// ".rp"="image/vnd.rn-realpix"
	// ".rpm"="audio/x-pn-realaudio-plugin"
	// ".rsml"="application/vnd.rn-rsml"
	// ".rt"="text/vnd.rn-realtext"
	// ".rtf"="application/msword"
	// ".rtf"="application/x-rtf"
	// ".rv"="video/vnd.rn-realvideo"
	// ".sam"="application/x-sam"
	// ".sat"="application/x-sat"
	// ".sdp"="application/sdp"
	// ".sdw"="application/x-sdw"
	// ".sit"="application/x-stuffit"
	// ".slb"="application/x-slb"
	// ".sld"="application/x-sld"
	// ".slk"="drawing/x-slk"
	// ".smi"="application/smil"
	// ".smil"="application/smil"
	// ".smk"="application/x-smk"
	// ".snd"="audio/basic"
	// ".sol"="text/plain"
	// ".sor"="text/plain"
	// ".spc"="application/x-pkcs7-certificates"
	// ".spl"="application/futuresplash"
	// ".spp"="text/xml"
	// ".ssm"="application/streamingmedia"
	// ".sst"="application/vnd.ms-pki.certstore"
	// ".stl"="application/vnd.ms-pki.stl"
	// ".stm"="text/html"
	// ".sty"="application/x-sty"
	// ".svg"="text/xml"
	// ".swf"="application/x-shockwave-flash"
	// ".tdf"="application/x-tdf"
	// ".tg4"="application/x-tg4"
	// ".tga"="application/x-tga"
	// ".tif"="image/tiff"
	// ".tif"="application/x-tif"
	// ".tiff"="image/tiff"
	// ".tld"="text/xml"
	// ".top"="drawing/x-top"
	// ".torrent"="application/x-bittorrent"
	// ".tsd"="text/xml"
	// ".txt"="text/plain"
	// ".uin"="application/x-icq"
	// ".uls"="text/iuls"
	// ".vcf"="text/x-vcard"
	// ".vda"="application/x-vda"
	// ".vdx"="application/vnd.visio"
	// ".vml"="text/xml"
	// ".vpg"="application/x-vpeg005"
	// ".vsd"="application/vnd.visio"
	// ".vsd"="application/x-vsd"
	// ".vss"="application/vnd.visio"
	// ".vst"="application/vnd.visio"
	// ".vst"="application/x-vst"
	// ".vsw"="application/vnd.visio"
	// ".vsx"="application/vnd.visio"
	// ".vtx"="application/vnd.visio"
	// ".vxml"="text/xml"
	// ".wav"="audio/wav"
	// ".wax"="audio/x-ms-wax"
	// ".wb1"="application/x-wb1"
	// ".wb2"="application/x-wb2"
	// ".wb3"="application/x-wb3"
	// ".wbmp"="image/vnd.wap.wbmp"
	// ".wiz"="application/msword"
	// ".wk3"="application/x-wk3"
	// ".wk4"="application/x-wk4"
	// ".wkq"="application/x-wkq"
	// ".wks"="application/x-wks"
	// ".wm"="video/x-ms-wm"
	// ".wma"="audio/x-ms-wma"
	// ".wmd"="application/x-ms-wmd"
	// ".wmf"="application/x-wmf"
	// ".wml"="text/vnd.wap.wml"
	// ".wmv"="video/x-ms-wmv"
	// ".wmx"="video/x-ms-wmx"
	// ".wmz"="application/x-ms-wmz"
	// ".wp6"="application/x-wp6"
	// ".wpd"="application/x-wpd"
	// ".wpg"="application/x-wpg"
	// ".wpl"="application/vnd.ms-wpl"
	// ".wq1"="application/x-wq1"
	// ".wr1"="application/x-wr1"
	// ".wri"="application/x-wri"
	// ".wrk"="application/x-wrk"
	// ".ws"="application/x-ws"
	// ".ws2"="application/x-ws"
	// ".wsc"="text/scriptlet"
	// ".wsdl"="text/xml"
	// ".wvx"="video/x-ms-wvx"
	// ".xdp"="application/vnd.adobe.xdp"
	// ".xdr"="text/xml"
	// ".xfd"="application/vnd.adobe.xfd"
	// ".xfdf"="application/vnd.adobe.xfdf"
	// ".xhtml"="text/html"
	// ".xls"="application/vnd.ms-excel"
	// ".xls"="application/x-xls"
	// ".xlw"="application/x-xlw"
	// ".xml"="text/xml"
	// ".xpl"="audio/scpls"
	// ".xq"="text/xml"
	// ".xql"="text/xml"
	// ".xquery"="text/xml"
	// ".xsd"="text/xml"
	// ".xsl"="text/xml"
	// ".xslt"="text/xml"
	// ".xwd"="application/x-xwd"
	// ".x_b"="application/x-x_b"
	// ".x_t"="application/x-x_t

	/** 获得文件类型 */
	public static String getContentType(String extension) {
		// ".jpg"="image/jpeg"
		if (extension == ".jpg") {
			return "image/jpeg";
		}
		// ".png"="image/png"
		if (extension == ".png") {
			return "image/png";
		}
		return "";
	}

	// TODO
	// HTTP
	// 状态码
	//
	// 含义
	//
	// 100
	// 客户端应当继续发送请求。这个临时响应是用来通知客户端它的部分请求已经被服务器接收，且仍未被拒绝。客户端应当继续发送请求的剩余部分，或者如果请求已经完成，忽略这个响应。服务器必须在请求完成后向客户端发送一个最终响应。
	// 101 服务器已经理解了客户端的请求，并将通过Upgrade
	// 消息头通知客户端采用不同的协议来完成这个请求。在发送完这个响应最后的空行后，服务器将会切换到在Upgrade 消息头中定义的那些协议。
	// 　　只有在切换新的协议更有好处的时候才应该采取类似措施。例如，切换到新的HTTP
	// 版本比旧版本更有优势，或者切换到一个实时且同步的协议以传送利用此类特性的资源。
	// 102 由WebDAV（RFC 2518）扩展的状态码，代表处理将被继续执行。
	// 200 请求已成功，请求所希望的响应头或数据体将随此响应返回。
	// 201 请求已经被实现，而且有一个新的资源已经依据请求的需要而建立，且其 URI 已经随Location
	// 头信息返回。假如需要的资源无法及时建立的话，应当返回 '202 Accepted'。
	// 202
	// 服务器已接受请求，但尚未处理。正如它可能被拒绝一样，最终该请求可能会也可能不会被执行。在异步操作的场合下，没有比发送这个状态码更方便的做法了。
	// 　　返回202状态码的响应的目的是允许服务器接受其他过程的请求（例如某个每天只执行一次的基于批处理的操作），而不必让客户端一直保持与服务器的连接直到批处理操作全部完成。在接受请求处理并返回202状态码的响应应当在返回的实体中包含一些指示处理当前状态的信息，以及指向处理状态监视器或状态预测的指针，以便用户能够估计操作是否已经完成。
	// 203
	// 服务器已成功处理了请求，但返回的实体头部元信息不是在原始服务器上有效的确定集合，而是来自本地或者第三方的拷贝。当前的信息可能是原始版本的子集或者超集。例如，包含资源的元数据可能导致原始服务器知道元信息的超级。使用此状态码不是必须的，而且只有在响应不使用此状态码便会返回200
	// OK的情况下才是合适的。
	// 204
	// 服务器成功处理了请求，但不需要返回任何实体内容，并且希望返回更新了的元信息。响应可能通过实体头部的形式，返回新的或更新后的元信息。如果存在这些头部信息，则应当与所请求的变量相呼应。
	// 　　如果客户端是浏览器的话，那么用户浏览器应保留发送了该请求的页面，而不产生任何文档视图上的变化，即使按照规范新的或更新后的元信息应当被应用到用户浏览器活动视图中的文档。
	// 　　由于204响应被禁止包含任何消息体，因此它始终以消息头后的第一个空行结尾。
	// 205
	// 服务器成功处理了请求，且没有返回任何内容。但是与204响应不同，返回此状态码的响应要求请求者重置文档视图。该响应主要是被用于接受用户输入后，立即重置表单，以便用户能够轻松地开始另一次输入。
	// 　　与204响应一样，该响应也被禁止包含任何消息体，且以消息头后的第一个空行结束。
	// 206 服务器已经成功处理了部分 GET 请求。类似于 FlashGet 或者迅雷这类的 HTTP
	// 下载工具都是使用此类响应实现断点续传或者将一个大文档分解为多个下载段同时下载。 　　该请求必须包含 Range
	// 头信息来指示客户端希望得到的内容范围，并且可能包含 If-Range 来作为请求条件。 　　响应必须包含如下的头部域：
	// 　　Content-Range 用以指示本次响应中返回的内容的范围；如果是 Content-Type 为 multipart/byteranges
	// 的多段下载，则每一 multipart 段中都应包含 Content-Range 域用以指示本段的内容范围。假如响应中包含
	// Content-Length，那么它的数值必须匹配它返回的内容范围的真实字节数。 　　Date 　　ETag 和/或
	// Content-Location，假如同样的请求本应该返回200响应。 　　Expires, Cache-Control，和/或
	// Vary，假如其值可能与之前相同变量的其他响应对应的值不同的话。 　　假如本响应请求使用了 If-Range
	// 强缓存验证，那么本次响应不应该包含其他实体头；假如本响应的请求使用了 If-Range
	// 弱缓存验证，那么本次响应禁止包含其他实体头；这避免了缓存的实体内容和更新了的实体头信息之间的不一致。否则，本响应就应当包含所有本应该返回200响应中应当返回的所有实体头部域。
	// 　　假如 ETag 或 Last-Modified
	// 头部不能精确匹配的话，则客户端缓存应禁止将206响应返回的内容与之前任何缓存过的内容组合在一起。 　　任何不支持 Range 以及
	// Content-Range 头的缓存都禁止缓存206响应返回的内容。
	// 207 由WebDAV(RFC
	// 2518)扩展的状态码，代表之后的消息体将是一个XML消息，并且可能依照之前子请求数量的不同，包含一系列独立的响应代码。
	// 300 被请求的资源有一系列可供选择的回馈信息，每个都有自己特定的地址和浏览器驱动的商议信息。用户或浏览器能够自行选择一个首选的地址进行重定向。
	// 　　除非这是一个 HEAD 请求，否则该响应应当包括一个资源特性及地址的列表的实体，以便用户或浏览器从中选择最合适的重定向地址。这个实体的格式由
	// Content-Type 定义的格式所决定。浏览器可能根据响应的格式以及浏览器自身能力，自动作出最合适的选择。当然，RFC
	// 2616规范并没有规定这样的自动选择该如何进行。 　　如果服务器本身已经有了首选的回馈选择，那么在 Location 中应当指明这个回馈的
	// URI；浏览器可能会将这个 Location 值作为自动重定向的地址。此外，除非额外指定，否则这个响应也是可缓存的。
	// 301 被请求的资源已永久移动到新位置，并且将来任何对此资源的引用都应该使用本响应返回的若干个 URI
	// 之一。如果可能，拥有链接编辑功能的客户端应当自动把请求的地址修改为从服务器反馈回来的地址。除非额外指定，否则这个响应也是可缓存的。
	// 　　新的永久性的 URI 应当在响应的 Location 域中返回。除非这是一个 HEAD 请求，否则响应的实体中应当包含指向新的 URI
	// 的超链接及简短说明。 　　如果这不是一个 GET 或者 HEAD
	// 请求，因此浏览器禁止自动进行重定向，除非得到用户的确认，因为请求的条件可能因此发生变化。 　　注意：对于某些使用 HTTP/1.0
	// 协议的浏览器，当它们发送的 POST 请求得到了一个301响应的话，接下来的重定向请求将会变成 GET 方式。
	// 302 请求的资源现在临时从不同的 URI
	// 响应请求。由于这样的重定向是临时的，客户端应当继续向原有地址发送以后的请求。只有在Cache-Control或Expires中进行了指定的情况下，这个响应才是可缓存的。
	// 　　新的临时性的 URI 应当在响应的 Location 域中返回。除非这是一个 HEAD 请求，否则响应的实体中应当包含指向新的 URI
	// 的超链接及简短说明。 　　如果这不是一个 GET 或者 HEAD
	// 请求，那么浏览器禁止自动进行重定向，除非得到用户的确认，因为请求的条件可能因此发生变化。 　　注意：虽然RFC 1945和RFC
	// 2068规范不允许客户端在重定向时改变请求的方法，但是很多现存的浏览器将302响应视作为303响应，并且使用 GET 方式访问在 Location
	// 中规定的 URI，而无视原先请求的方法。状态码303和307被添加了进来，用以明确服务器期待客户端进行何种反应。
	// 303 对应当前请求的响应可以在另一个 URI 上被找到，而且客户端应当采用 GET
	// 的方式访问那个资源。这个方法的存在主要是为了允许由脚本激活的POST请求输出重定向到一个新的资源。这个新的 URI
	// 不是原始资源的替代引用。同时，303响应禁止被缓存。当然，第二个请求（重定向）可能被缓存。 　　新的 URI 应当在响应的 Location
	// 域中返回。除非这是一个 HEAD 请求，否则响应的实体中应当包含指向新的 URI 的超链接及简短说明。 　　注意：许多 HTTP/1.1 版以前的
	// 浏览器不能正确理解303状态。如果需要考虑与这些浏览器之间的互动，302状态码应该可以胜任，因为大多数的浏览器处理302响应时的方式恰恰就是上述规范要求客户端处理303响应时应当做的。
	// 304 如果客户端发送了一个带条件的 GET
	// 请求且该请求已被允许，而文档的内容（自上次访问以来或者根据请求的条件）并没有改变，则服务器应当返回这个状态码。304响应禁止包含消息体，因此始终以消息头后的第一个空行结尾。
	// 　　该响应必须包含以下的头信息： 　　Date，除非这个服务器没有时钟。假如没有时钟的服务器也遵守这些规则，那么代理服务器以及客户端可以自行将
	// Date 字段添加到接收到的响应头中去（正如RFC 2068中规定的一样），缓存机制将会正常工作。 　　ETag 和/或
	// Content-Location，假如同样的请求本应返回200响应。 　　Expires,
	// Cache-Control，和/或Vary，假如其值可能与之前相同变量的其他响应对应的值不同的话。
	// 　　假如本响应请求使用了强缓存验证，那么本次响应不应该包含其他实体头；否则（例如，某个带条件的 GET
	// 请求使用了弱缓存验证），本次响应禁止包含其他实体头；这避免了缓存了的实体内容和更新了的实体头信息之间的不一致。
	// 　　假如某个304响应指明了当前某个实体没有缓存，那么缓存系统必须忽视这个响应，并且重复发送不包含限制条件的请求。
	// 　　假如接收到一个要求更新某个缓存条目的304响应，那么缓存系统必须更新整个条目以反映所有在响应中被更新的字段的值。
	// 305 被请求的资源必须通过指定的代理才能被访问。Location 域中将给出指定的代理所在的 URI
	// 信息，接收者需要重复发送一个单独的请求，通过这个代理才能访问相应资源。只有原始服务器才能建立305响应。 　　注意：RFC
	// 2068中没有明确305响应是为了重定向一个单独的请求，而且只能被原始服务器建立。忽视这些限制可能导致严重的安全后果。
	// 306 在最新版的规范中，306状态码已经不再被使用。
	// 307 请求的资源现在临时从不同的URI
	// 响应请求。由于这样的重定向是临时的，客户端应当继续向原有地址发送以后的请求。只有在Cache-Control或Expires中进行了指定的情况下，这个响应才是可缓存的。
	// 　　新的临时性的URI 应当在响应的 Location 域中返回。除非这是一个HEAD 请求，否则响应的实体中应当包含指向新的URI
	// 的超链接及简短说明。因为部分浏览器不能识别307响应，因此需要添加上述必要信息以便用户能够理解并向新的 URI 发出访问请求。
	// 　　如果这不是一个GET 或者 HEAD 请求，那么浏览器禁止自动进行重定向，除非得到用户的确认，因为请求的条件可能因此发生变化。
	// 400 1、语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。 　　2、请求参数有误。
	// 401 当前请求需要用户验证。该响应必须包含一个适用于被请求资源的 WWW-Authenticate
	// 信息头用以询问用户信息。客户端可以重复提交一个包含恰当的 Authorization 头信息的请求。如果当前请求已经包含了
	// Authorization
	// 证书，那么401响应代表着服务器验证已经拒绝了那些证书。如果401响应包含了与前一个响应相同的身份验证询问，且浏览器已经至少尝试了一次验证，那么浏览器应当向用户展示响应中包含的实体信息，因为这个实体信息中可能包含了相关诊断信息。参见RFC
	// 2617。
	// 402 该状态码是为了将来可能的需求而预留的。
	// 403 服务器已经理解请求，但是拒绝执行它。与401响应不同的是，身份验证并不能提供任何帮助，而且这个请求也不应该被重复提交。如果这不是一个
	// HEAD
	// 请求，而且服务器希望能够讲清楚为何请求不能被执行，那么就应该在实体内描述拒绝的原因。当然服务器也可以返回一个404响应，假如它不希望让客户端获得任何信息。
	// 404
	// 请求失败，请求所希望得到的资源未被在服务器上发现。没有信息能够告诉用户这个状况到底是暂时的还是永久的。假如服务器知道情况的话，应当使用410状态码来告知旧资源因为某些内部的配置机制问题，已经永久的不可用，而且没有任何可以跳转的地址。404这个状态码被广泛应用于当服务器不想揭示到底为何请求被拒绝或者没有其他适合的响应可用的情况下。
	// 405 请求行中指定的请求方法不能被用于请求相应的资源。该响应必须返回一个Allow 头信息用以表示出当前资源能够接受的请求方法的列表。 　　鉴于
	// PUT，DELETE
	// 方法会对服务器上的资源进行写操作，因而绝大部分的网页服务器都不支持或者在默认配置下不允许上述请求方法，对于此类请求均会返回405错误。
	// 406 请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。 　　除非这是一个 HEAD
	// 请求，否则该响应就应当返回一个包含可以让用户或者浏览器从中选择最合适的实体特性以及地址列表的实体。实体的格式由 Content-Type
	// 头中定义的媒体类型决定。浏览器可以根据格式及自身能力自行作出最佳选择。但是，规范中并没有定义任何作出此类自动选择的标准。
	// 407 　与401响应类似，只不过客户端必须在代理服务器上进行身份验证。代理服务器必须返回一个 Proxy-Authenticate
	// 用以进行身份询问。客户端可以返回一个 Proxy-Authorization 信息头用以验证。参见RFC 2617。
	// 408 请求超时。客户端没有在服务器预备等待的时间内完成一个请求的发送。客户端可以随时再次提交这一请求而无需进行任何更改。
	// 409
	// 由于和被请求的资源的当前状态之间存在冲突，请求无法完成。这个代码只允许用在这样的情况下才能被使用：用户被认为能够解决冲突，并且会重新提交新的请求。该响应应当包含足够的信息以便用户发现冲突的源头。
	// 　　冲突通常发生于对 PUT 请求的处理中。例如，在采用版本检查的环境下，某次 PUT
	// 提交的对特定资源的修改请求所附带的版本信息与之前的某个（第三方）请求向冲突，那么此时服务器就应该返回一个409错误，告知用户请求无法完成。此时，响应实体中很可能会包含两个冲突版本之间的差异比较，以便用户重新提交归并以后的新版本。
	// 410
	// 被请求的资源在服务器上已经不再可用，而且没有任何已知的转发地址。这样的状况应当被认为是永久性的。如果可能，拥有链接编辑功能的客户端应当在获得用户许可后删除所有指向这个地址的引用。如果服务器不知道或者无法确定这个状况是否是永久的，那么就应该使用404状态码。除非额外说明，否则这个响应是可缓存的。
	// 　　410响应的目的主要是帮助网站管理员维护网站，通知用户该资源已经不再可用，并且服务器拥有者希望所有指向这个资源的远端连接也被删除。这类事件在限时、增值服务中很普遍。同样，410响应也被用于通知客户端在当前服务器站点上，原本属于某个个人的资源已经不再可用。当然，是否需要把所有永久不可用的资源标记为'410
	// Gone'，以及是否需要保持此标记多长时间，完全取决于服务器拥有者。
	// 411 服务器拒绝在没有定义 Content-Length 头的情况下接受请求。在添加了表明请求消息体长度的有效 Content-Length
	// 头之后，客户端可以再次提交该请求。
	// 412
	// 服务器在验证在请求的头字段中给出先决条件时，没能满足其中的一个或多个。这个状态码允许客户端在获取资源时在请求的元信息（请求头字段数据）中设置先决条件，以此避免该请求方法被应用到其希望的内容以外的资源上。
	// 413
	// 服务器拒绝处理当前请求，因为该请求提交的实体数据大小超过了服务器愿意或者能够处理的范围。此种情况下，服务器可以关闭连接以免客户端继续发送此请求。
	// 　　如果这个状况是临时的，服务器应当返回一个 Retry-After 的响应头，以告知客户端可以在多少时间以后重新尝试。
	// 414 请求的URI 长度超过了服务器能够解释的长度，因此服务器拒绝对该请求提供服务。这比较少见，通常的情况包括：
	// 　　本应使用POST方法的表单提交变成了GET方法，导致查询字符串（Query String）过长。 　　重定向URI
	// “黑洞”，例如每次重定向把旧的 URI 作为新的 URI 的一部分，导致在若干次重定向后 URI 超长。
	// 　　客户端正在尝试利用某些服务器中存在的安全漏洞攻击服务器。这类服务器使用固定长度的缓冲读取或操作请求的 URI，当 GET
	// 后的参数超过某个数值后，可能会产生缓冲区溢出，导致任意代码被执行[1]。没有此类漏洞的服务器，应当返回414状态码。
	// 415 对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝。
	// 416 如果请求中包含了 Range 请求头，并且 Range 中指定的任何数据范围都与当前资源的可用范围不重合，同时请求中又没有定义
	// If-Range 请求头，那么服务器就应当返回416状态码。 　　假如 Range
	// 使用的是字节范围，那么这种情况就是指请求指定的所有数据范围的首字节位置都超过了当前资源的长度。服务器也应当在返回416状态码的同时，包含一个
	// Content-Range 实体头，用以指明当前资源的长度。这个响应也被禁止使用 multipart/byteranges 作为其
	// Content-Type。
	// 417 在请求头 Expect
	// 中指定的预期内容无法被服务器满足，或者这个服务器是一个代理服务器，它有明显的证据证明在当前路由的下一个节点上，Expect 的内容无法被满足。
	// 421
	// 从当前客户端所在的IP地址到服务器的连接数超过了服务器许可的最大范围。通常，这里的IP地址指的是从服务器上看到的客户端地址（比如用户的网关或者代理服务器地址）。在这种情况下，连接数的计算可能涉及到不止一个终端用户。
	// 422
	// 从当前客户端所在的IP地址到服务器的连接数超过了服务器许可的最大范围。通常，这里的IP地址指的是从服务器上看到的客户端地址（比如用户的网关或者代理服务器地址）。在这种情况下，连接数的计算可能涉及到不止一个终端用户。
	// 422 请求格式正确，但是由于含有语义错误，无法响应。（RFC 4918 WebDAV）423 Locked 　　当前资源被锁定。（RFC
	// 4918 WebDAV）
	// 424 由于之前的某个请求发生的错误，导致当前请求失败，例如 PROPPATCH。（RFC 4918 WebDAV）
	// 425 在WebDav Advanced Collections 草案中定义，但是未出现在《WebDAV 顺序集协议》（RFC 3658）中。
	// 426 客户端应当切换到TLS/1.0。（RFC 2817）
	// 449 由微软扩展，代表请求应当在执行完适当的操作后进行重试。
	// 500 服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理。一般来说，这个问题都会在服务器的程序码出错时出现。
	// 501 服务器不支持当前请求所需要的某个功能。当服务器无法识别请求的方法，并且无法支持其对任何资源的请求。
	// 502 作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应。
	// 503
	// 由于临时的服务器维护或者过载，服务器当前无法处理请求。这个状况是临时的，并且将在一段时间以后恢复。如果能够预计延迟时间，那么响应中可以包含一个
	// Retry-After 头用以标明这个延迟时间。如果没有给出这个 Retry-After 信息，那么客户端应当以处理500响应的方式处理它。
	// 　　注意：503状态码的存在并不意味着服务器在过载的时候必须使用它。某些服务器只不过是希望拒绝客户端的连接。
	// 504
	// 作为网关或者代理工作的服务器尝试执行请求时，未能及时从上游服务器（URI标识出的服务器，例如HTTP、FTP、LDAP）或者辅助服务器（例如DNS）收到响应。
	// 　　注意：某些代理服务器在DNS查询超时时会返回400或者500错误
	// 505 服务器不支持，或者拒绝支持在请求中使用的 HTTP
	// 版本。这暗示着服务器不能或不愿使用与客户端相同的版本。响应中应当包含一个描述了为何版本不被支持以及服务器支持哪些协议的实体。
	// 506 由《透明内容协商协议》（RFC
	// 2295）扩展，代表服务器存在内部配置错误：被请求的协商变元资源被配置为在透明内容协商中使用自己，因此在一个协商处理中不是一个合适的重点。
	// 507 服务器无法存储完成请求所必须的内容。这个状况被认为是临时的。WebDAV (RFC 4918)
	// 509 服务器达到带宽限制。这不是一个官方的状态码，但是仍被广泛使用。
	// 510 获取资源所需要的策略并没有没满足。（RFC 2774）
	// TODO

	/** 拼接参数 */
	public static String encodeParams(Map<String, Object> params) {
		if (params == null || params.size() == 0) {
			return null;
		}
		StringBuilder strBuilder = new StringBuilder();
		Object value = null;
		for (String key : params.keySet()) {
			value = params.get(key);
			if (value == null || value.equals("")) {
				continue;
			}
			try {
				String valueStr = URLEncoder.encode(value.toString(),
						HTTP.UTF_8);
				strBuilder.append(key + "=" + valueStr + "&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (strBuilder.length() == 0)
			return null;
		strBuilder.deleteCharAt(strBuilder.length() - 1);
		return strBuilder.toString();
	}

	/** 拼接json */
	public static String encodeJson(Map<String, Object> params) {
		if (params == null || params.size() == 0) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();
		Object value = null;

		sb.append("{");
		for (String key : params.keySet()) {
			value = params.get(key);
			if (value == null || value.equals("")) {
				continue;
			}

			sb.append("\"");
			sb.append(key);
			sb.append("\"");
			sb.append(":");
			sb.append("\"");
			sb.append(value.toString());
			sb.append("\"");
			sb.append(",");
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("}\n\n");

		return sb.toString();
	}

	/**
	 * 产生11位的boundary
	 */
	public static String getBoundry() {
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; t++) {
			long time = System.currentTimeMillis() + t;
			if (time % 3 == 0) {
				_sb.append((char) time % 9);
			} else if (time % 3 == 1) {
				_sb.append((char) (65 + time % 26));
			} else {
				_sb.append((char) (97 + time % 26));
			}
		}
		return _sb.toString();
	}

}
