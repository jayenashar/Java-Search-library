package org.jayenashar.search.examples.shoppingplan;

import au.edu.unsw.cse.jayen.search.AStarSearch;
import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;
import au.edu.unsw.cse.jayen.search.StateSpaceSearchProblem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
                cases[caseIndex] = new Case(reader);
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

    private static class Item implements Comparable<Item> {
        private final String  name;
        private final boolean perishable;
        private short minPrice = Short.MAX_VALUE;

        private Item(final String s) {
            this(s.replaceFirst("!$", ""), s.endsWith("!"));
        }

        private Item(final String name, final boolean perishable) {
            this.name = name;
            this.perishable = perishable;
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

    private static class Case {
        private final short       priceOfGas;
        private final List<Item>  items;
        private final List<Store> stores;

        private Case(final BufferedReader reader) throws IOException {
            final String[] itemsStoresGas = reader.readLine().split(" ");
            final byte numItems = Byte.parseByte(itemsStoresGas[0]);
            final byte numStores = Byte.parseByte(itemsStoresGas[1]);
            priceOfGas = Short.parseShort(itemsStoresGas[2]);

            items = Arrays.stream(reader.readLine().split(" ")).map(Item::new).collect(Collectors.toList());
            assert numItems == items.size();

            stores = new ArrayList<>(numStores);
            for (int storeIndex = 0; storeIndex < numStores; storeIndex++)
                stores.add(new Store(reader.readLine()));
        }

        private double minimumSpend() {
            final AStarSearch<State> search = new AStarSearch<>(state -> {
                final int itemsMinPrice = state.itemsToBuy.stream().mapToInt(Item::getMinPrice).sum();
                final double goNearestStoreAndHome = stores.stream().mapToDouble(store -> (state.isHome() ?
                                                                                           store.goHomeDistance() :
                                                                                           Math.hypot(store.xPos - state.xPos,
                                                                                                      store.yPos - state.yPos)) +
                                                                                          store.goHomeDistance()).min()
                                                           .getAsDouble();
                return itemsMinPrice + goNearestStoreAndHome * priceOfGas;
            }, 1.0000001);
            final double minimumSpend = search.search(new StateSpaceSearchProblem<State>() {
                @Override
                public Iterable<State> initialStates() {
                    return Collections.singleton(new State(Collections.emptyList(),
                                                           Collections.emptyList(),
                                                           items,
                                                           (short) 0,
                                                           (short) 0,
                                                           0));
                }

                @Override
                public boolean isGoal(final State state) {
                    return state.itemsToBuy.size() == 0;
                }

                @Override
                public Iterable<ActionStatePair<State>> successor(final State state) {
                    return stores.stream().filter(store -> !state.storesVisited.contains(store)).map(store -> {
                        // calculate these once per store, even if i may not go home
                        final double priceOfGasToStore = Math.hypot(state.xPos - store.xPos, state.yPos - store.yPos) *
                                                         Case.this.priceOfGas;
                        final double priceOfGasToHome = store.goHomePrice();
                        final List<Store> storesVisited = new ArrayList<>(state.storesVisited.size() + 1);
                        storesVisited.addAll(state.storesVisited);
                        storesVisited.add(store);

                        final List<Store.Item> itemsMayBy = store.items.stream().filter(storeItem -> storeItem.isToBuy(state))
                                                                       .collect(Collectors.toList());
                        final Stream<List<Store.Item>> allCombinationsOfItems =
                                StreamSupport.stream(((Iterable<List<Store.Item>>) () -> new Iterator<List<Store.Item>>() {
                                    private long combinationBitFlag = 1;

                                    @Override
                                    public boolean hasNext() {
                                        return combinationBitFlag < 1 << itemsMayBy.size();
                                    }

                                    @Override
                                    public List<Store.Item> next() {
                                        long combinationBitFlag2 = combinationBitFlag;
                                        int itemIndex = 0;
                                        final List<Store.Item> itemsToBuy = new ArrayList<>(itemsMayBy.size());
                                        do {
                                            if ((combinationBitFlag2 & 1) == 1)
                                                itemsToBuy.add(itemsMayBy.get(itemIndex));
                                            combinationBitFlag2 >>= 1;
                                            ++itemIndex;
                                        } while (combinationBitFlag2 != 0);
                                        ++combinationBitFlag;
                                        return itemsToBuy;
                                    }
                                }).spliterator(), false);
                        return allCombinationsOfItems.map(storeItemsToBuy -> {
                            final int itemsPrice = storeItemsToBuy.stream().mapToInt(item -> item.price).sum();
                            final boolean isGoingHome = state.itemsToBuy.size() == storeItemsToBuy.size() ||
                                                        storeItemsToBuy.stream().anyMatch(Store.Item::isPerishable);
                            final double cost = itemsPrice + priceOfGasToStore + (isGoingHome ? priceOfGasToHome : 0);

                            final List<Item> itemsToBuy = new ArrayList<>(storeItemsToBuy.size());
                            storeItemsToBuy.stream().map(storeItem -> storeItem.item).forEach(itemsToBuy::add);
                            final List<Item> itemsBought = new ArrayList<>(state.itemsBought.size() + itemsToBuy.size());
                            itemsBought.addAll(state.itemsBought);
                            itemsBought.addAll(itemsToBuy);
                            final List<Item> itemsToBuy2 = new ArrayList<>(state.itemsToBuy.size() - itemsToBuy.size());
                            itemsToBuy2.addAll(state.itemsToBuy);
                            itemsToBuy2.removeAll(itemsToBuy);

                            final Action action = () -> cost;
                            final State state2 = new State(storesVisited,
                                                           itemsBought,
                                                           itemsToBuy2,
                                                           isGoingHome ? 0 : store.xPos,
                                                           isGoingHome ? 0 : store.yPos,
                                                           state.spend + cost);
                            return new ActionStatePair<>(action, state2);
                        });
                    }).flatMap(Function.identity()).collect(Collectors.toList());
                }
            }).stream().mapToDouble(Action::cost).sum();
            System.out.println("States explored: " + search.statesExplored());
            return minimumSpend;
        }

        private class Store {
            private final short      xPos;
            private final short      yPos;
            private final List<Item> items;
            private final double     goHomePrice;
            private final double     goHomeDistance;

            private Store(final String s) {
                final String[] store = s.split(" ", 3);
                xPos = Short.parseShort(store[0]);
                yPos = Short.parseShort(store[1]);
                items = Arrays.stream(store[2].split(" ")).map(Item::new).collect(Collectors.toList());
                goHomeDistance = Math.hypot(xPos, yPos);
                goHomePrice = goHomeDistance * priceOfGas;
            }

            private double goHomeDistance() {
                return goHomeDistance;
            }

            private double goHomePrice() {
                return goHomePrice;
            }

            private class Item {
                private final ShoppingPlan.Item item;
                private final short             price;

                private Item(final String s) {
                    this(s.split(":"));
                }

                private Item(final String[] strings) {
                    this(strings[0], Short.parseShort(strings[1]));
                }

                private Item(final String name, final short price) {
                    this(Case.this.items.stream().filter(item -> item.name.equals(name)).findAny().get(), price);
                }

                private Item(final ShoppingPlan.Item item, final short price) {
                    this.item = item;
                    this.price = price;
                    item.setMinPrice(price);
                }

                private boolean isToBuy(final State state) {
                    return state.itemsToBuy.contains(item);
                }

                private boolean isPerishable() {
                    return item.perishable;
                }
            }
        }

        private class State {
            private final List<Store> storesVisited;
            private final List<Item>  itemsBought;
            private final List<Item>  itemsToBuy;
            private final short       xPos;
            private final short       yPos;
            private final double      spend;

            private State(final List<Store> storesVisited,
                          final List<Item> itemsBought,
                          final List<Item> itemsToBuy,
                          final short xPos,
                          final short yPos,
                          final double spend) {
                this.storesVisited = storesVisited;
                this.itemsBought = itemsBought;
                this.itemsToBuy = itemsToBuy;
                this.xPos = xPos;
                this.yPos = yPos;
                this.spend = spend;

                Collections.sort(itemsBought);
                Collections.sort(itemsToBuy);
            }

            @Override
            public int hashCode() {
                return Objects.hash(itemsBought, itemsToBuy, xPos, yPos);
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                final State state = (State) o;
                return xPos == state.xPos &&
                       yPos == state.yPos &&
                       Objects.equals(itemsBought, state.itemsBought) &&
                       Objects.equals(itemsToBuy, state.itemsToBuy);
            }

            private boolean isHome() {
                return xPos == 0 && yPos == 0;
            }
        }
    }
}
