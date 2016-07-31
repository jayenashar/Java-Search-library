package org.jayenashar.search.examples.shoppingplan;

import au.edu.unsw.cse.jayen.search.AStarSearch;
import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.search.Search;
import au.edu.unsw.cse.jayen.search.StateSpaceSearchProblem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * https://code.google.com/codejam/contest/32003/dashboard#s=p3
 * <p>
 * Created by jayen on 23/07/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ShoppingPlan {
    /**
     * @param args one input filename
     */
    public static void main(String[] args) throws IOException {
        final String filename = args[0];
        final BufferedReader reader = new BufferedReader(new FileReader(filename));
        final byte numCases = Byte.parseByte(reader.readLine());
        final Case[] cases = new Case[numCases];
        final double[] minimumSpends = new double[numCases];
        IntStream.range(0, numCases).sequential().forEachOrdered(caseIndex -> {
            try {
//                cases[caseIndex] = new Case(reader);
                cases[caseIndex] = new CaseCPP(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        IntStream.range(0, numCases).parallel().forEach(caseIndex -> {
            minimumSpends[caseIndex] = cases[caseIndex].minimumSpend();
            System.out.println("Case #" + (caseIndex + 1) + ": " + String.format("%.7f", minimumSpends[caseIndex]));
        });
        System.out.println();
        IntStream.range(0, numCases).forEachOrdered(caseIndex -> System.out
                .println("Case #" + (caseIndex + 1) + ": " + String.format("%.7f", minimumSpends[caseIndex])));
    }

    private static class Case {
        final         List<Item>  items;
        final         List<Store> stores;
        final         Store       home;
        private final short       priceOfGas;
        private final byte        numStores;

        private Case(final BufferedReader reader) throws IOException {
            final String[] itemsStoresGas = reader.readLine().split(" ");
            final byte numItems = Byte.parseByte(itemsStoresGas[0]);
            numStores = Byte.parseByte(itemsStoresGas[1]);
            priceOfGas = Short.parseShort(itemsStoresGas[2]);

            final String[] itemsArray = reader.readLine().split(" ");
            items = new ArrayList<>(itemsArray.length);
            for (int i = 0; i < itemsArray.length; i++) {
                final Item item = new Item(itemsArray[i], i);
                items.add(item);
            }
            assert numItems == items.size();

            stores = new ArrayList<>(numStores + 1);
            for (int storeIndex = 0; storeIndex < numStores; storeIndex++)
                stores.add(new Store(reader.readLine(), stores));
            home = new Store("0 0 ", stores);
            stores.add(home);

        }

        double minimumSpend() {
            final Search<State> search = new AStarSearch<>(state -> {
                int itemsMinPrice = 0;
                for (int index = state.itemsToBuy.nextSetBit(0); index >= 0; index = state.itemsToBuy.nextSetBit(index + 1))
                    itemsMinPrice += items.get(index).getMinPrice();
                final double goHomePrice = state.store.goStorePrice(home);
                return itemsMinPrice + goHomePrice;
            });
            final double minimumSpend = search.search(new StateSpaceSearchProblem<State>() {
                @Override
                public Iterable<State> initialStates() {
                    final BitSet itemsToBuy = new BitSet(items.size());
                    itemsToBuy.set(0, items.size());
                    return Collections.singleton(new State(itemsToBuy, home, false, null));
                }

                @Override
                public boolean isGoal(final State state) {
                    return state.itemsToBuy.isEmpty();
                }

                @Override
                public Iterable<ActionStatePair<State>> successor(final State state) {
                    final ArrayList<ActionStatePair<State>> successors = new ArrayList<>();
                    final int sizeExcludingHome = stores.size() - 1;
                    for (int s = 0; s < sizeExcludingHome; s++) {
                        final Store store = stores.get(s);
                        final double priceOfGasToStore = state.store.goStorePrice(store);
                        final double priceOfGasToHomeToStore = state.store.goStorePrice(home) + home.goStorePrice(store);

                        final BitSet itemsToBuy2 = new BitSet(state.itemsToBuy);
                        final boolean perishable2 = false;
                        short price2 = 0;
                        if (state.store != store) {
                            for (Store.Item storeItem : store.items) {
                                if (itemsToBuy2.get(storeItem.item.index) &&
                                    storeItem.price <= storeItem.item.getMinPrice() &&
                                    !storeItem.item.perishable) {
                                    itemsToBuy2.clear(storeItem.item.index);
//                                perishable2 |= storeItem.item.perishable;
                                    price2 += storeItem.price;
                                }
                            }
                        }

                        if (!state.itemsToBuy.equals(itemsToBuy2))
                            addSuccessor(state,
                                         successors,
                                         store,
                                         priceOfGasToStore,
                                         priceOfGasToHomeToStore,
                                         null,
                                         itemsToBuy2,
                                         perishable2,
                                         price2);
                        else
                            for (Store.Item storeItem : store.items) {
                                if (storeItem.isToBuy(state)) {
                                    final int itemToBuy3 = storeItem.item.index;
                                    final BitSet itemsToBuy3 = state.itemsToBuy.cloneClear(itemToBuy3);
                                    final boolean perishable3 = storeItem.item.perishable;
                                    final int price3 = storeItem.price;

                                    addSuccessor(state,
                                                 successors,
                                                 store,
                                                 priceOfGasToStore,
                                                 priceOfGasToHomeToStore,
                                                 storeItem,
                                                 itemsToBuy3,
                                                 perishable3,
                                                 price3);
                                }
                            }
                    }
                    return successors;
                }

                private void addSuccessor(final State state,
                                          final ArrayList<ActionStatePair<State>> successors,
                                          final Store store,
                                          final double priceOfGasToStore,
                                          final double priceOfGasToHomeToStore,
                                          final Store.Item lastBought3,
                                          final BitSet itemsToBuy3,
                                          final boolean hasPerishable3,
                                          final int price3) {
                    final Action action3;
                    final State state3;
                    if (itemsToBuy3.isEmpty()) {
                        if (state.store == store)
                            action3 = () -> price3 + store.goStorePrice(home);
                        else if (state.hasPerishable)
                            action3 = () -> priceOfGasToHomeToStore + price3 + store.goStorePrice(home);
                        else
                            action3 = () -> priceOfGasToStore + price3 + store.goStorePrice(home);
                        state3 = new State(itemsToBuy3, home, false, null);
                    } else if (state.store == store) {
                        action3 = () -> price3;
                        state3 = new State(itemsToBuy3, store, state.hasPerishable || hasPerishable3, lastBought3);
                    } else if (state.hasPerishable) {
                        action3 = () -> priceOfGasToHomeToStore + price3;
                        state3 = new State(itemsToBuy3, store, hasPerishable3, lastBought3);
                    } else {
                        action3 = () -> priceOfGasToStore + price3;
                        state3 = new State(itemsToBuy3, store, hasPerishable3, lastBought3);
                    }

                    successors.add(new ActionStatePair<>(action3, state3));
                }

            }).stream().mapToDouble(Action::cost).sum();
            System.out.println("States explored: " + search.statesExplored());
            return minimumSpend;
        }

        private static class Item implements Comparable<Item> {
            private final String  name;
            private final boolean perishable;
            private final int     index;
            private short minPrice = Short.MAX_VALUE;

            private Item(final String s, final int index) {
                this(s.replaceFirst("!$", ""), s.endsWith("!"), index);
            }

            private Item(final String name, final boolean perishable, final int index) {
                this.name = name;
                this.perishable = perishable;
                this.index = index;
            }

            private short getMinPrice() {
                return minPrice;
            }

            private void setMinPrice(short price) {
                if (price < minPrice)
                    minPrice = price;
            }

            @Override
            public int compareTo(final Item o) {
                return name.compareTo(o.name);
            }
        }

        class Store {
            private final short    xPos;
            private final short    yPos;
            private final Item[]   items;
            private final double[] goStorePrices; // last one reserved for home
            private       int      index;

            private Store(final String s, final List<Store> stores) {
                final String[] store = s.split(" ", 3);
                xPos = Short.parseShort(store[0]);
                yPos = Short.parseShort(store[1]);
                if (store[2].isEmpty())
                    items = new Item[0];
                else
                    items = Arrays.stream(store[2].split(" ")).map(Item::new).toArray(Item[]::new);
                goStorePrices = new double[numStores + 1];
                for (int s2 = 0; s2 < stores.size(); s2++) {
                    Store store2 = stores.get(s2);
                    final double goStorePrice = Math.hypot(store2.xPos - xPos, store2.yPos - yPos) * priceOfGas;
                    store2.goStorePrices[stores.size()] = goStorePrice;
                    goStorePrices[s2] = goStorePrice;
                }
                index = stores.size();
            }

            private double goStorePrice(final Store store2) {
                return goStorePrices[stores.indexOf(store2)];
            }

            private class Item {
                private final Case.Item item;
                private final short     price;

                private Item(final String s) {
                    this(s.split(":"));
                }

                private Item(final String[] strings) {
                    this(strings[0], Short.parseShort(strings[1]));
                }

                private Item(final String name, final short price) {
                    this(Case.this.items.stream().filter(item -> item.name.equals(name)).findAny().get(), price);
                }

                private Item(final Case.Item item, final short price) {
                    this.item = item;
                    this.price = price;
                    item.setMinPrice(price);
                }

                private boolean isToBuy(final State state) {
                    return state.itemsToBuy.get(item.index) &&
                           (state.store != Store.this || state.lastBought == null || state.lastBought.item.index < item.index);
                }
            }
        }

        private class State {
            private final BitSet     itemsToBuy;
            private final Store      store;
            private final boolean    hasPerishable;
            private final Store.Item lastBought;
            private final int        hash;

            private State(final BitSet itemsToBuy,
                          final Store store,
                          final boolean hasPerishable,
                          final Store.Item lastBought) {
                this.itemsToBuy = itemsToBuy;
                this.store = store;
                this.hasPerishable = hasPerishable;
                this.lastBought = lastBought;
                hash = (itemsToBuy.hashCode() * stores.size() + store.index) * 2 + (hasPerishable ? 0 : 1);
            }

            @Override
            public int hashCode() {
                return hash;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                final State state = (State) o;
                return Objects.equals(itemsToBuy, state.itemsToBuy) &&
                       Objects.equals(store, state.store) &&
                       Objects.equals(hasPerishable, state.hasPerishable);
            }
        }
    }

    private static class BitSet {
        private static final int WORD_MASK = -1;
        private int word;

        public BitSet(final int size) {
            assert size <= Integer.SIZE;
        }

        public BitSet(final BitSet set) {
            word = set.word;
        }

        public void set(int bitIndex, boolean value) {
            if (value)
                set(bitIndex);
            else
                clear(bitIndex);
        }

        public void clear(int bitIndex) {
            word &= ~(1 << bitIndex);
        }

        public void set(int bitIndex) {
            word |= (1 << bitIndex);
        }

        public void set(int fromIndex, int toIndex) {
            long firstWordMask = WORD_MASK << fromIndex;
            long lastWordMask = WORD_MASK >>> -toIndex;
            word |= (firstWordMask & lastWordMask);
        }

        public int nextSetBit(int fromIndex) {
            int word = this.word & (WORD_MASK << fromIndex);

            if (word != 0)
                return Integer.numberOfTrailingZeros(word);
            return -1;
        }

        public boolean isEmpty() {
            return word == 0;
        }

        public boolean get(int bitIndex) {
            return (word & 1 << bitIndex) != 0;
        }

        private BitSet cloneClear(final int bitIndex) {
            final BitSet result = new BitSet(Integer.SIZE);
            result.word = word & ~(1 << bitIndex);
            return result;
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(Object obj) {
            BitSet set = (BitSet) obj;
            return word == set.word;
        }

        public int hashCode() {
            return word;
        }
    }

    private static class CaseCPP extends Case {
        private final int      n;
        private final int      m;
        private final double[] F;
        private final Store[]  P;
        private final short[]  M;
        private final int[]    bad;

        private CaseCPP(final BufferedReader reader) throws IOException {
            super(reader);
            n = items.size();
            m = stores.size();
            F = new double[(1 << n) * m * 2];
            Arrays.fill(F, -1);
            P = stores.toArray(new Store[m]);
            M = new short[m * n];
            Arrays.fill(M, (short) -1);
            for (int i = 0, storesSize = stores.size(); i < storesSize; i++) {
                for (final Store.Item item : stores.get(i).items) {
                    M[i * n + item.item.index] = item.price;
                }
            }
            bad = new int[n];
            for (int i = 0, itemsSize = items.size(); i < itemsSize; i++) {
                bad[i] = items.get(i).perishable ? 1 : 0;
            }
        }

        double f(final int mask, final int cur, final int b) {
            final int ret = (mask * m + cur) * 2 + b;
            if (F[ret] == -1) {
                F[ret] = Double.POSITIVE_INFINITY;
                if (mask == 0) {
                    if (cur == m - 1) F[ret] = 0;
                    return F[ret];
                }
                for (int i = 0; i < n; i++)
                    if ((mask & 1 << i) != 0 && M[cur * n + i] != -1) for (int j = 0; j < m; j++) {
                        final int tmp = mask ^ (1 << i);
                        if (j == cur) {
                            F[ret] = Math.min(F[ret], f(tmp, j, b | bad[i]) + M[cur * n + i]);
                        } else if ((bad[i] | b) != 0) {
                            F[ret] = Math.min(F[ret], f(tmp, j, 0) + M[cur * n + i] + (abs(P[cur]) + abs(P[j])));
                        } else {
                            F[ret] = Math.min(F[ret], f(tmp, j, 0) + M[cur * n + i] + abs(P[j], P[cur]));
                        }
                    }
                //cout << mask<<' '<<cur<<' '<<b<<' '<<F[ret]<<endl;
            }
            return F[ret];
        }

        private double abs(final Store store, final Store store1) {
            return store.goStorePrice(store1);
        }

        @Override
        double minimumSpend() {
            double sol = Double.POSITIVE_INFINITY;
            for (int i = 0; i < m; i++) {
                sol = Math.min(sol, f((1 << n) - 1, i, 0) + abs(P[i]));
            }
            return sol;
        }

        private double abs(final Store store) {
            return store.goStorePrice(home);
        }
    }
}
