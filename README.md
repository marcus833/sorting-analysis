# Análise de Desempenho de Algoritmos de Ordenação em Ambientes Concorrentes e Paralelos

**Disciplina:** Computação Paralela  
**Instituição:** Universidade de Fortaleza — UNIFOR  
**Aluno:** Marcus Vinicius Herbster Ferreira  
**Período:** 2025.1  

---

## Resumo

Este trabalho apresenta uma análise comparativa do desempenho de quatro algoritmos de ordenação clássicos — Bubble Sort, Quick Sort, Merge Sort e Selection Sort — em suas versões sequenciais e paralelas, implementados na linguagem Java. O objetivo central é investigar como a paralelização impacta o tempo de execução de cada algoritmo sob diferentes tamanhos de entrada e diferentes quantidades de threads. Os resultados são registrados em arquivo CSV e analisados estatisticamente, permitindo identificar padrões de desempenho e determinar quais algoritmos se beneficiam mais do processamento paralelo.

---

## 1. Introdução

A busca por eficiência computacional é um tema central na ciência da computação moderna. Com o avanço dos processadores multicore, a programação paralela tornou-se uma abordagem essencial para extrair o máximo de desempenho das máquinas atuais. Nesse contexto, algoritmos de ordenação — amplamente utilizados em sistemas reais — representam um excelente objeto de estudo para comparar abordagens sequenciais e paralelas.

Este trabalho implementa e avalia quatro algoritmos de ordenação:

- **Bubble Sort:** algoritmo simples de complexidade O(n²), que compara pares adjacentes repetidamente. Paralelizado via Odd-Even Transposition Sort, onde fases pares e ímpares são executadas em paralelo por múltiplas threads.
- **Quick Sort:** algoritmo de divisão e conquista com complexidade média O(n log n). Paralelizado com `ForkJoinPool` e pivot por mediana de 3, dividindo as partições entre threads recursivamente. A versão serial usa pilha iterativa para evitar estouro de pilha em arrays ordenados.
- **Merge Sort:** algoritmo estável de complexidade O(n log n), naturalmente paralelizável pela sua estrutura de divisão. Paralelizado com `RecursiveAction` via `ForkJoinPool`, com fallback serial para subarrays abaixo de um limiar.
- **Selection Sort:** algoritmo O(n²) que seleciona o mínimo a cada iteração. Paralelizado pela busca simultânea do mínimo em segmentos do array via `ExecutorService`.

A abordagem consiste em executar cada algoritmo em modo serial (1 thread) e em modo paralelo (2, 4 e 8 threads), variando o tamanho do array e o tipo de dado (aleatório, ordenado e invertido), coletando 5 amostras de tempo por configuração.

---

## 2. Metodologia

### 2.1 Ambiente de Execução

- **Linguagem:** Java 17
- **Gerenciador de dependências:** Maven 3.x
- **APIs de concorrência:** `ForkJoinPool`, `RecursiveAction`, `ExecutorService`, `Executors.newFixedThreadPool`
- **Sistema Operacional:** Windows 11
- **Medição de tempo:** `System.nanoTime()`, convertido para milissegundos

### 2.2 Framework de Testes

Foi desenvolvido um framework próprio (`TestRunner`) responsável por:

- Gerar arrays de três naturezas distintas:
  - **Aleatório (`random`):** valores gerados com `Random` e semente fixa (42) para reprodutibilidade
  - **Ordenado (`sorted`):** array já em ordem crescente — melhor caso para Bubble Sort
  - **Invertido (`reverse`):** array em ordem decrescente — pior caso para vários algoritmos
- Copiar o array original antes de cada execução, garantindo que todos os algoritmos recebam os mesmos dados de entrada
- Medir o tempo de execução com precisão de nanossegundos

### 2.3 Configurações dos Experimentos

| Parâmetro          | Valores                           |
|--------------------|-----------------------------------|
| Tamanhos de array  | 1.000 / 5.000 / 10.000 / 20.000  |
| Tipos de dados     | Aleatório, Ordenado, Invertido    |
| Threads (paralelo) | 2, 4, 8                           |
| Amostras por teste | 5                                 |
| Total de amostras  | 960                               |

> O tamanho máximo foi limitado a 20.000 elementos para viabilizar a execução dos algoritmos O(n²) em tempo razoável, mantendo a comparabilidade entre todos os métodos.

### 2.4 Análise Estatística

Para cada configuração (algoritmo + tipo + tamanho + threads), foram coletadas 5 amostras de tempo. A análise considera:

- **Média:** tempo médio das 5 amostras por configuração
- **Speedup:** razão `T_serial / T_paralelo` calculada sobre as médias
- **Variação por tipo de dado:** comparação do impacto de arrays aleatórios, ordenados e invertidos no mesmo algoritmo

---

## 3. Estrutura do Projeto

```
sorting-analysis/
├── pom.xml
├── results/
│   └── resultados.csv
└── src/
    └── main/
        └── java/
            └── com/
                └── sorting/
                    ├── Main.java
                    ├── algorithms/
                    │   ├── BubbleSort.java
                    │   ├── QuickSort.java
                    │   ├── MergeSort.java
                    │   └── SelectionSort.java
                    ├── parallel/
                    │   ├── ParallelBubbleSort.java
                    │   ├── ParallelQuickSort.java
                    │   ├── ParallelMergeSort.java
                    │   └── ParallelSelectionSort.java
                    ├── framework/
                    │   └── TestRunner.java
                    └── results/
                        └── CsvWriter.java
```

---

## 4. Resultados e Discussão

### 4.1 Comportamento Serial — dados aleatórios

Tempos médios medidos (ms) para arrays aleatórios:

| Algoritmo      | 1.000  | 5.000  | 10.000  | 20.000   |
|----------------|--------|--------|---------|----------|
| Bubble Sort    | 1,6 ms | 12,5 ms| 72,9 ms | 463,7 ms |
| Quick Sort     | 0,6 ms | 0,6 ms | 0,9 ms  | 1,5 ms   |
| Merge Sort     | 0,9 ms | 0,5 ms | 1,1 ms  | 2,7 ms   |
| Selection Sort | 1,4 ms | 6,0 ms | 19,1 ms | 89,3 ms  |

O crescimento quadrático de Bubble Sort e Selection Sort é evidente: de 1.000 para 20.000 elementos (20×), o Bubble Sort levou cerca de 290× mais tempo, confirmando O(n²) na prática. Quick Sort e Merge Sort mantiveram tempos abaixo de 3 ms mesmo para 20.000 elementos, demonstrando o comportamento O(n log n).

### 4.2 Impacto do Tipo de Dado

O tipo de dado impactou significativamente os resultados:

- **Bubble Sort** foi muito mais rápido em arrays ordenados (~84 ms para 20k) do que em aleatórios (~464 ms) ou invertidos (~272 ms), pois o número de trocas cai drasticamente quando o array já está ordenado.
- **Quick Sort** com pivot mediana de 3 manteve desempenho consistente nos três tipos, eliminando o pior caso clássico de arrays ordenados. Para 20k: ~1,5 ms (aleatório), ~0,6 ms (ordenado), ~0,9 ms (invertido).
- **Merge Sort** apresentou tempos estáveis independente do tipo de dado, confirmando sua complexidade O(n log n) garantida no pior caso.
- **Selection Sort** foi praticamente indiferente ao tipo de dado (~85–103 ms para todos os tipos em 20k), pois sempre varre o array completo em busca do mínimo.

### 4.3 Impacto da Paralelização — 20.000 elementos aleatórios

Tempos médios paralelos (ms) para 20.000 elementos aleatórios:

| Algoritmo       | Serial   | 2 threads  | 4 threads  | 8 threads  |
|-----------------|----------|------------|------------|------------|
| Bubble Sort     | 463,7 ms | 34.328 ms  | 37.877 ms  | 45.566 ms  |
| Quick Sort      | 1,5 ms   | 4,0 ms     | 3,2 ms     | 2,7 ms     |
| Merge Sort      | 2,7 ms   | 2,1 ms     | 2,9 ms     | 2,1 ms     |
| Selection Sort  | 89,3 ms  | 274,3 ms   | 305,8 ms   | 332,3 ms   |

Resultado relevante: Bubble Sort e Selection Sort paralelos foram significativamente mais lentos que suas versões seriais. O overhead de criação e sincronização de threads superou qualquer ganho de processamento paralelo para esses algoritmos O(n²).

### 4.4 Speedup Real Medido

Speedup calculado como `T_serial / T_paralelo` (médias das 5 amostras, 20.000 elementos aleatórios):

| Algoritmo               | 2 threads | 4 threads | 8 threads |
|-------------------------|-----------|-----------|-----------|
| Parallel Merge Sort     | 1,29x     | 0,93x     | 1,29x     |
| Parallel Quick Sort     | 0,38x     | 0,47x     | 0,56x     |
| Parallel Bubble Sort    | 0,013x    | 0,012x    | 0,010x    |
| Parallel Selection Sort | 0,33x     | 0,29x     | 0,27x     |

O Merge Sort foi o único que obteve speedup acima de 1x com paralelização. Quick Sort paralelo ficou abaixo do serial em todos os casos com 20.000 elementos — o overhead do `ForkJoinPool` é significativo para entradas de tamanho moderado. Para Bubble Sort e Selection Sort, a paralelização causou degradação severa de desempenho.

### 4.5 Observações Relevantes

- O Parallel Bubble Sort (Odd-Even Transposition) cria e destrói um pool de threads a cada fase de ordenação. Para arrays de 10.000 elementos, isso gerou tempos acima de 15 segundos contra ~73 ms serial — o overhead é proibitivo.
- O Parallel Quick Sort com `ForkJoinPool` apresentou instabilidade entre amostras, indicando contenção de threads nas partições pequenas.
- O Merge Sort paralelo foi o mais consistente: a estrutura recursiva independente entre as metades permite divisão de trabalho eficiente sem contenção de memória.
- Para arrays de 1.000 elementos, nenhum algoritmo paralelo superou o serial — o overhead de paralelização sempre domina em entradas pequenas.
- A implementação iterativa do Quick Sort serial (com pilha explícita e pivot mediana de 3) foi essencial para evitar `StackOverflowError` em arrays ordenados ou invertidos de grande porte.

---

## 5. Conclusão

Este trabalho demonstrou na prática os conceitos de computação paralela aplicados a algoritmos de ordenação. Os resultados reais confirmam que:

1. Nem todo algoritmo se beneficia de paralelização — Bubble Sort e Selection Sort ficaram significativamente mais lentos na versão paralela, pois o overhead de sincronização superou qualquer ganho computacional.
2. Merge Sort foi o algoritmo que mais se beneficiou da paralelização, graças à sua estrutura recursiva naturalmente independente entre as metades.
3. O tamanho da entrada é determinante: para arrays de até 5.000 elementos, a versão serial é consistentemente mais rápida que a paralela para todos os algoritmos testados.
4. O tipo de dado impacta mais os algoritmos O(n²) do que os O(n log n). Quick Sort com mediana de 3 mostrou-se robusto a qualquer tipo de entrada.
5. Aumentar o número de threads nem sempre melhora o desempenho — para a maioria dos algoritmos testados, 8 threads não trouxe ganho adicional significativo sobre 2 threads, e em alguns casos piorou o resultado.

A experiência reforça que a escolha de um algoritmo de ordenação deve considerar não apenas a complexidade teórica, mas o tamanho real da entrada, o padrão dos dados e o ambiente de execução disponível.

---

## 6. Referências

- CORMEN, T. H. et al. **Introdução a Algoritmos**. 3. ed. MIT Press, 2009.
- GOETZ, B. et al. **Java Concurrency in Practice**. Addison-Wesley, 2006.
- Oracle. **ForkJoinPool (Java SE 17)**. Disponível em: https://docs.oracle.com/en/java/api/java.base/java/util/concurrent/ForkJoinPool.html
- Oracle. **ExecutorService (Java SE 17)**. Disponível em: https://docs.oracle.com/en/java/api/java.base/java/util/concurrent/ExecutorService.html
- SEDGEWICK, R.; WAYNE, K. **Algorithms**. 4. ed. Addison-Wesley, 2011.

---

## 7. Anexos — Como Executar

```bash
# 1. Clone o repositório
git clone https://github.com/marcus833/sorting-analysis.git
cd sorting-analysis

# 2. Compile o projeto
mvn compile

# 3. Execute os testes (gera o CSV automaticamente)
mvn exec:java

# 4. O arquivo de resultados estará em:
# results/resultados.csv
```

Todos os códigos-fonte estão disponíveis no repositório GitHub abaixo.

---

## Link do Repositório

**https://github.com/marcus833/sorting-analysis**

---

*Trabalho desenvolvido para a disciplina de Computação Paralela — UNIFOR, 2025.1*
