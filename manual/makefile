INCLUDES = ../preview.png
SECTIONS = section*.tex

all: Jhin3\ user\ manual.pdf

clean:
	rm manual.aux
	rm manual.log
	rm manual.out
	rm manual.toc
	rm section*.aux
	rm Jhin3\ user\ manual.pdf

Jhin3\ user\ manual.pdf: manual.tex $(INCLUDES) $(SECTIONS)
	pdflatex manual.tex
	pdflatex manual.tex
	mv manual.pdf Jhin3\ user\ manual.pdf
