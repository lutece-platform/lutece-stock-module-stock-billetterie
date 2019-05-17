
function init_tiny_mce(type, editorLocale) {
	var sheets = document.styleSheets,
	stylesheet = sheets[(sheets.length - 1)];
	for( var i in document.styleSheets ){
		if( sheets[i].href && sheets[i].href.indexOf("page_template_styles.css") > -1 ) {
		    stylesheet = sheets[i];
		    break;
		}
	}
		
	var cssMap = new Map();
	var classes = stylesheet.cssRules;
	for (var x = 0; x < classes.length; x++) {
		if (classes[x].style) {
			var styleStr = {};
			for (var i = 0; i < classes[x].style.length; i++) {
				styleStr[classes[x].style.item(i)] = classes[x].style.getPropertyValue(classes[x].style.item(i));
			}
			cssMap.set(classes[x].selectorText, styleStr)
		}
	}
	
	 var colorMap =  [
		 	"FFFFFF", "FFFFFF",
			"000000", "000000",
			"DF225A", "DF225A",
			"157FEC", "157FEC",
			"7FAD53", "7FAD53",
			"A0A0A0", "A0A0A0",
			"6063CC", "6063CC",
			"3A4364", "3A4364",
			"838FA9", "838FA9",
			"8B91A7", "8B91A7",
			"5A69A2", "5A69A2",
			"1DB8BD", "1DB8BD",
			"FA526C", "FA526C",
			"5D6D87", "5D6D87",
			"465073", "465073",
			"19CE0F", "19CE0F",
			"2CA8FE", "2CA8FE",
			"FFB236", "FFB236",
			"FE3636", "FE3636"
	];
	 
	 var styles =  [
			{title: "Titre 1", format: "h1"},
			{title: "Titre 2", format: "h2"},
			{title: "Titre 3", format: "h3"},
			{title: "Titre 4", format: "h4"},
			{title: "Titre 5", format: "h5"},
			{title: "Titre 6", format: "h6"},
			{ title: 'Texte défaut', inline: 'span', remove: 'all' },
			{ title: 'Texte petit',	 inline: 'span', styles: cssMap.get('.text-small')  },
			{ title: 'Texte grand',	 inline: 'span', styles: cssMap.get('.text-large')  },
			{ title: 'Texte plus grand', inline: 'span', styles: cssMap.get('.text-xlarge')  }
		];
	var fontsizes = '8px 9px 10px 11px 12px 14px 16px 18px 20px 22px 24px 26px 28px 36px 48px 72px';
	
	if (type == 'inlite') {
		tinymce.init({
			selector: 'div.richtext',
			theme: 'inlite',
			textcolor_cols: 6,
			textcolor_rows: 10,
			textcolor_map: colorMap,
			document_base_url : "${webapp_url}",
			plugins: 'image table link paste contextmenu textpattern autolink lutece',
			insert_toolbar: 'quickimage quicktable link lutece',
			selection_toolbar: 'bold italic | quicklink h2 h3 blockquote',
			paste_as_text: true,
			paste_word_valid_elements: "b,strong,i,em,h1,h2,h3,p,br",
			content_css: "${cssFiles}",
			style_formats: styles,
			fontsize_formats: fontsizes,
			inline: true,
			language: editorLocale,
			paste_data_images: true,
	                lutece_library_img_default_space_id: '#dskey{library.img.defaultSpace.id}',
	                lutece_library_pdf_default_space_id: '#dskey{library.pdf.defaultSpace.id}'
			});
	} else {
		tinyMCE.init({
			// General options ${editorLocale}
			selector : "textarea.richtext",
			theme : "modern",
			textcolor_cols: 6,
			textcolor_rows: 10,
			textcolor_map: colorMap,
			document_base_url : "${webapp_url}",
			width : "${editorWidth}",
			menubar : false,
			language: editorLocale,
			plugins: [
	       "lutece advlist autolink link image lists charmap print preview hr anchor pagebreak ",
	       "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
	       "save table contextmenu directionality template paste textcolor"
			],
			paste_as_text: true,
			// Extended elements
			extended_valid_elements : "iframe[style|src|width|height|name|align|frameborder|scrolling],script[src|type]",
			paste_word_valid_elements: "b,strong,i,em,h1,h2,h3,p,br",
			content_css: "${cssFiles}",
			style_formats: styles,
			fontsize_formats: fontsizes,
			toolbar: "undo redo | paste | styleselect fontsizeselect | bold italic  | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lutece luteceImg lutecePdf link media | print preview fullscreen code",
			contextmenu: "lutece luteceImg lutecePdf link image | paste copy cut | inserttable | cell row column deletetable",
	                lutece_library_img_default_space_id: '#dskey{library.img.defaultSpace.id}',
	                lutece_library_pdf_default_space_id: '#dskey{library.pdf.defaultSpace.id}'
		});
	}
	stylesheet.disabled = true;
}